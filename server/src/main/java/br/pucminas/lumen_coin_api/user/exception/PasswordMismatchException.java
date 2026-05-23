package br.pucminas.lumen_coin_api.user.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("A nova senha e a confirmação não conferem");
    }
}
