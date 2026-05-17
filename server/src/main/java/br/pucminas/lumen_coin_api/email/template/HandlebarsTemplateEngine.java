package br.pucminas.lumen_coin_api.email.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HandlebarsTemplateEngine {

    private final Handlebars handlebars;

    public HandlebarsTemplateEngine() {
        TemplateLoader loader = new ClassPathTemplateLoader("/templates/email", ".hbs");
        this.handlebars = new Handlebars(loader);
    }

    public String render(String templateName, Object context) {
        try {
            Template template = handlebars.compile(templateName);
            return template.apply(context);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to render email template: " + templateName, e);
        }
    }
}
