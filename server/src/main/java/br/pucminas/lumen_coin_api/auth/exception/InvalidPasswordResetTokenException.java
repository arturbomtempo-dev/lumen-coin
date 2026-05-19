package br.pucminas.lumen_coin_api.auth.exception;

public class InvalidPasswordResetTokenException extends RuntimeException {
    public InvalidPasswordResetTokenException() {
        super("Token de recuperação de senha inválido ou expirado.");
    }
}
