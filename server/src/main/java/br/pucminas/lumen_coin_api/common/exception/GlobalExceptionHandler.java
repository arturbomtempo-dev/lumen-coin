package br.pucminas.lumen_coin_api.common.exception;

import br.pucminas.lumen_coin_api.auth.exception.InvalidPasswordResetTokenException;
import br.pucminas.lumen_coin_api.benefit_redemption.exception.BenefitRedemptionNotFoundException;
import br.pucminas.lumen_coin_api.benefit_redemption.exception.RedemptionAlreadyUsedException;
import br.pucminas.lumen_coin_api.benefit_redemption.exception.UnauthorizedRedemptionException;
import br.pucminas.lumen_coin_api.coin_transfer.exception.InsufficientBalanceException;
import br.pucminas.lumen_coin_api.common.dto.ErrorResponse;
import br.pucminas.lumen_coin_api.benefit.exception.BenefitNotFoundException;
import br.pucminas.lumen_coin_api.course.exception.CourseNotFoundException;
import br.pucminas.lumen_coin_api.storage.exception.StorageException;
import br.pucminas.lumen_coin_api.user.exception.CompanyNotFoundException;
import br.pucminas.lumen_coin_api.user.exception.CnpjAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.CpfAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.IncorrectPasswordException;
import br.pucminas.lumen_coin_api.user.exception.PasswordMismatchException;
import br.pucminas.lumen_coin_api.user.exception.StudentInstitutionCourseMismatchException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordResetToken(
            InvalidPasswordResetTokenException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(
            CourseNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(BenefitNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBenefitNotFound(
            BenefitNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(BenefitRedemptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBenefitRedemptionNotFound(
            BenefitRedemptionNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(RedemptionAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleRedemptionAlreadyUsed(
            RedemptionAlreadyUsedException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedRedemptionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedRedemption(
            UnauthorizedRedemptionException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorage(
            StorageException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFound(
            CompanyNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailConflict(
            EmailAlreadyInUseException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(CnpjAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleCnpjConflict(
            CnpjAlreadyInUseException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(CpfAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleCpfConflict(
            CpfAlreadyInUseException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(
            InsufficientBalanceException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    @ExceptionHandler(StudentInstitutionCourseMismatchException.class)
    public ResponseEntity<ErrorResponse> handleStudentInstitutionCourseMismatch(
            StudentInstitutionCourseMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return build(HttpStatus.BAD_REQUEST, "Validation Failed", errors, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Parâmetro inválido: '" + ex.getName() + "'";
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "Requisição inválida ou malformada.", request);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword(
            IncorrectPasswordException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(
            PasswordMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "E-mail ou senha inválidos.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", "Você não tem permissão para acessar este recurso.", request);
    }

    @ExceptionHandler({ AuthenticationException.class, InsufficientAuthenticationException.class })
    public ResponseEntity<ErrorResponse> handleAuthentication(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Autenticação necessária para acessar este recurso.",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request);
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status.value(), error, message, request.getRequestURI()));
    }
}
