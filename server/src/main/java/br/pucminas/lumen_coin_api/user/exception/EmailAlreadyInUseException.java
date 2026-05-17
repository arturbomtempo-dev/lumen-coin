package br.pucminas.lumen_coin_api.user.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("O e-mail '" + email + "' já está cadastrado");
    }
}
