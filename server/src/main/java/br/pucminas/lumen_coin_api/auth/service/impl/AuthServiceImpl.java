package br.pucminas.lumen_coin_api.auth.service.impl;

import br.pucminas.lumen_coin_api.auth.dto.request.ForgotPasswordRequest;
import br.pucminas.lumen_coin_api.auth.dto.request.LoginRequest;
import br.pucminas.lumen_coin_api.auth.dto.request.ResetPasswordRequest;
import br.pucminas.lumen_coin_api.auth.dto.response.AuthResponse;
import br.pucminas.lumen_coin_api.auth.dto.response.MeResponse;
import br.pucminas.lumen_coin_api.auth.entity.PasswordResetToken;
import br.pucminas.lumen_coin_api.auth.exception.InvalidPasswordResetTokenException;
import br.pucminas.lumen_coin_api.auth.repository.PasswordResetTokenRepository;
import br.pucminas.lumen_coin_api.auth.service.AuthService;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.security.JwtService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import br.pucminas.lumen_coin_api.user.entity.User;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int RESET_TOKEN_EXPIRATION_HOURS = 1;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.url:http://localhost:5173}")
    private String appUrl;

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        response.addCookie(jwtService.buildAuthCookie(principal));

        return new AuthResponse("Logged in successfully");
    }

    @Override
    public AuthResponse logout(HttpServletResponse response) {
        response.addCookie(jwtService.buildClearCookie());
        return new AuthResponse("Logged out successfully");
    }

    @Override
    public MeResponse me(UserPrincipal principal) {
        return new MeResponse(
                principal.getUserId().toString(),
                principal.getName(),
                principal.getEmail(),
                principal.getRole().name()
        );
    }

    @Override
    @Transactional
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUserId(user.getId());
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpiresAt(LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRATION_HOURS));
            passwordResetTokenRepository.save(resetToken);

            String resetLink = appUrl + "/reset-password?token=" + resetToken.getToken();
            emailService.sendPasswordReset(user.getEmail(), user.getName(), resetLink, RESET_TOKEN_EXPIRATION_HOURS);
        }
        return new AuthResponse("Se o e-mail estiver cadastrado, você receberá as instruções em breve.");
    }

    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(InvalidPasswordResetTokenException::new);

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidPasswordResetTokenException();
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(InvalidPasswordResetTokenException::new);

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        return new AuthResponse("Senha redefinida com sucesso.");
    }
}
