package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.user.entity.User;
import br.pucminas.lumen_coin_api.user.exception.IncorrectPasswordException;
import br.pucminas.lumen_coin_api.user.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Centraliza a regra de troca de senha compartilhada por todos os tipos de
 * usuário (aluno, professor, empresa e instituição).
 *
 * <p>
 * Valida se a senha atual confere, se a nova senha e sua confirmação são iguais
 * e aplica o hash, evitando que essa mesma lógica seja duplicada em cada
 * {@code *ServiceImpl}.
 */
@Component
@RequiredArgsConstructor
public class UserPasswordManager {

    private final PasswordEncoder passwordEncoder;

    public void changePassword(User user, String currentPassword, String newPassword, String confirmNewPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IncorrectPasswordException();
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new PasswordMismatchException();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
