package br.pucminas.lumen_coin_api.user.exception;

public class CpfAlreadyInUseException extends RuntimeException {
    public CpfAlreadyInUseException(String cpf) {
        super("The CPF '" + cpf + "' is already registered");
    }
}
