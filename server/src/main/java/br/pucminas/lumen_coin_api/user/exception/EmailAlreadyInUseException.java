package br.pucminas.lumen_coin_api.user.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("The e-mail address '" + email + "' is already registered");
    }
}
