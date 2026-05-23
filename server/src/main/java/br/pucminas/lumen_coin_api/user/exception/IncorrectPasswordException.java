package br.pucminas.lumen_coin_api.user.exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("A senha atual informada está incorreta");
    }
}
