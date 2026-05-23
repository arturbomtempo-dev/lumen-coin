# Lumen Coin API - Email System

The API includes a robust, asynchronous email delivery system built on **Spring Mail** and **Handlebars** templating. This guide covers how to send emails, integrate email functionality into services, and create custom email templates.

---

## Architecture Overview

The email system is organized into a dedicated `email` module with the following structure:

```
br.pucminas.lumen_coin_api.email
├── dto/
│   └── *EmailContext.java          - Data transfer objects for template context
├── service/
│   ├── EmailService.java           - Service interface
│   └── impl/
│       └── EmailServiceImpl.java    - Service implementation
└── template/
    └── HandlebarsTemplateEngine.java - Template rendering engine
```

### Components

| Component                  | Responsibility                                                       |
| -------------------------- | -------------------------------------------------------------------- |
| `EmailService`             | Interface defining all available email operations                    |
| `EmailServiceImpl`         | Concrete implementation; sends emails asynchronously via `@Async`    |
| `HandlebarsTemplateEngine` | Renders HTML templates using Handlebars syntax from the classpath    |
| `*EmailContext` records    | Transfer objects that provide data to templates                      |
| `.hbs` templates           | HTML email templates stored in `src/main/resources/templates/email/` |

---

## Configuration

Email functionality is controlled via environment variables:

| Variable           | Type    | Purpose                                             |
| ------------------ | ------- | --------------------------------------------------- |
| `MAIL_HOST`        | String  | SMTP server hostname                                |
| `MAIL_PORT`        | Integer | SMTP port (typically `587` for TLS)                 |
| `MAIL_USERNAME`    | String  | SMTP username / sender email                        |
| `MAIL_PASSWORD`    | String  | SMTP password or app-specific token                 |
| `APP_MAIL_FROM`    | String  | "From" address in sent emails (e.g., `noreply@...`) |
| `APP_MAIL_ENABLED` | Boolean | Enable/disable email sending (default: `true`)      |

### Spring Mail Properties (in `application.properties`)

```properties
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

app.mail.from=${APP_MAIL_FROM}
app.mail.enabled=${APP_MAIL_ENABLED}
```

---

## Sending Emails

### Using the EmailService

Inject `EmailService` into any service or controller:

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EmailService emailService;

    public void registerUser(RegisterUserRequest request) {
        User user = new User(...);
        userRepository.save(user);

        emailService.sendWelcome(user.getEmail(), user.getName());
    }
}
```

### Available Methods

Currently available email operations:

```java
public interface EmailService {
    void sendWelcome(String to, String name);
}
```

**Method Details:**

- **`sendWelcome(String to, String name)`**
    - Recipient email address: `to`
    - User's display name: `name`
    - Renders the `welcome.hbs` template with the user's name
    - Executes asynchronously (non-blocking)
    - Logs success or failure; errors do not propagate to the caller

### Asynchronous Execution

All email operations are annotated with `@Async` and execute on a background thread. This means:

- Email sending **does not block** the HTTP request/response cycle
- Failures in email delivery **do not crash** the application or return error responses
- Monitor the application logs for delivery success/failure

Ensure `@EnableAsync` is enabled in your Spring configuration (typically in the main `@SpringBootApplication` class or a `@Configuration` class).

---

## Creating Custom Email Templates

### Template Location

All email templates are stored in:

```
src/main/resources/templates/email/
```

Templates must use the `.hbs` (Handlebars) file extension.

### Creating a New Template

**Step 1: Create the template file**

Create a new file, e.g., `src/main/resources/templates/email/password-reset.hbs`:

```html
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta charset="UTF-8" />
        <title>Reset Your Password</title>
    </head>
    <body>
        <h1>Hello {{name}},</h1>
        <p>We received a request to reset your password.</p>
        <p><a href="{{resetLink}}">Click here to reset your password</a></p>
        <p>This link expires in {{expirationHours}} hours.</p>
        <p>If you did not request this, please ignore this email.</p>
    </body>
</html>
```

**Step 2: Create a context DTO**

Create a record in `src/main/java/br/pucminas/lumen_coin_api/email/dto/`:

```java
package br.pucminas.lumen_coin_api.email.dto;

public record PasswordResetEmailContext(
    String name,
    String resetLink,
    Integer expirationHours
) {
}
```

**Step 3: Add a method to `EmailService`**

Update the interface:

```java
public interface EmailService {
    void sendWelcome(String to, String name);
    void sendPasswordReset(String to, String name, String resetLink, Integer expirationHours);
}
```

**Step 4: Implement the method**

Add the implementation in `EmailServiceImpl`:

```java
@Async
@Override
public void sendPasswordReset(String to, String name, String resetLink, Integer expirationHours) {
    if (!enabled) {
        log.debug("Mail disabled - skipping password reset email to {}", to);
        return;
    }
    try {
        String html = templateEngine.render("password-reset",
            new PasswordResetEmailContext(name, resetLink, expirationHours));
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("Reset Your Password");
        helper.setText(html, true);
        mailSender.send(message);
        log.info("Password reset email sent to {}", to);
    } catch (Exception e) {
        log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
    }
}
```

**Step 5: Use the new email method**

```java
emailService.sendPasswordReset(
    user.getEmail(),
    user.getName(),
    "https://app.lumencoin.com/reset?token=abc123",
    24
);
```

---

## Handlebars Template Syntax

Handlebars provides simple templating:

| Syntax              | Purpose                              |
| ------------------- | ------------------------------------ |
| `{{variable}}`      | Output a variable from the context   |
| `{{#if condition}}` | Conditional block                    |
| `{{#each array}}`   | Loop over an array                   |
| `{{this}}`          | Reference the current iteration item |

### Example: Conditional Content

```html
<p>Hello {{name}},</p>
{{#if isPremium}}
<p>Thank you for being a premium member!</p>
{{else}}
<p>Upgrade to premium for exclusive benefits.</p>
{{/if}}
```

### Example: Looping

```html
<ul>
    {{#each achievements}}
    <li>{{this}}</li>
    {{/each}}
</ul>
```

For advanced Handlebars features, see the [official documentation](https://handlebarsjs.com/).

---

## Best Practices

### 1. Always Disable Email in Test Environments

Set `APP_MAIL_ENABLED=false` in test `.env` files to prevent real emails from being sent during development.

### 2. Use Appropriate Email Addresses

- Use `noreply@` addresses for transactional emails (registration, password reset, etc.)
- Do not use user-facing email addresses as the "From" sender

### 3. Keep Templates Simple

- Avoid complex logic in templates; move business logic to the context DTO
- Templates should focus on presentation only

### 4. Handle Failures Gracefully

- Email failures are logged but do not propagate; the business operation continues
- Monitor application logs regularly for failed email deliveries
- Consider implementing a retry mechanism for critical emails (future enhancement)

### 5. Use Descriptive Variable Names

- In context DTOs and templates, use clear, descriptive names
- Avoid abbreviations; `resetTokenExpiration` is better than `resetTokExp`

### 6. Validate Email Addresses

- Validate recipient email addresses at the service layer before calling `EmailService`
- Use Jakarta Validation annotations on request DTOs

### 7. Test Locally

- Use a local SMTP mock service (e.g., Mailhog, MailSlurper) during development
- Or set `APP_MAIL_ENABLED=false` to skip actual sending

---

## Troubleshooting

### Emails Not Sending

1. **Check if email is disabled**: Verify `APP_MAIL_ENABLED=true` in your `.env`
2. **Check SMTP credentials**: Ensure `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, and `MAIL_PASSWORD` are correct
3. **Check firewall/network**: SMTP port (usually 587) may be blocked by your network
4. **Review application logs**: Look for error messages from `EmailServiceImpl` or Spring Mail

### Template Not Found

- Verify the template filename matches exactly (case-sensitive)
- Ensure the file is in `src/main/resources/templates/email/` with a `.hbs` extension
- Rebuild the project (`mvn clean install`) to ensure resources are copied

### Email Content Rendering Issues

- Check for typos in template variable names (e.g., `{{name}}` vs `{{username}}`)
- Verify the context DTO provides all variables used in the template
- Test the template with different context values

---

## Example: Complete Email Workflow

### Scenario: Send a course enrollment confirmation

**1. Create the template** (`course-enrolled.hbs`):

```html
<!DOCTYPE html>
<html>
    <body>
        <h2>Welcome, {{studentName}}!</h2>
        <p>You have successfully enrolled in:</p>
        <h3>{{courseName}}</h3>
        <p>Start date: {{startDate}}</p>
        <p>Instructor: {{instructorName}}</p>
    </body>
</html>
```

**2. Create the context DTO**:

```java
public record CourseEnrollmentEmailContext(
    String studentName,
    String courseName,
    String startDate,
    String instructorName
) {
}
```

**3. Add service method**:

```java
// In EmailService interface:
void sendCourseEnrollment(String to, String studentName, String courseName,
    String startDate, String instructorName);

// In EmailServiceImpl:
@Async
@Override
public void sendCourseEnrollment(String to, String studentName, String courseName,
    String startDate, String instructorName) {
    if (!enabled) {
        log.debug("Mail disabled - skipping course enrollment email to {}", to);
        return;
    }
    try {
        String html = templateEngine.render("course-enrolled",
            new CourseEnrollmentEmailContext(studentName, courseName, startDate, instructorName));
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("Course Enrollment Confirmation: " + courseName);
        helper.setText(html, true);
        mailSender.send(message);
        log.info("Course enrollment email sent to {}", to);
    } catch (Exception e) {
        log.error("Failed to send course enrollment email to {}: {}", to, e.getMessage());
    }
}
```

**4. Use it in a service**:

```java
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EmailService emailService;

    public void enrollStudentInCourse(Student student, Course course) {
        Enrollment enrollment = new Enrollment(student, course);
        enrollmentRepository.save(enrollment);

        emailService.sendCourseEnrollment(
            student.getEmail(),
            student.getName(),
            course.getTitle(),
            course.getStartDate().toString(),
            course.getInstructor().getName()
        );
    }
}
```

---

## Further Reading

- [Spring Mail Documentation](https://spring.io/guides/gs/sending-email/)
- [Handlebars Documentation](https://handlebarsjs.com/)
- [SMTP Configuration Best Practices](https://mailtrap.io/blog/smtp-configuration/)
