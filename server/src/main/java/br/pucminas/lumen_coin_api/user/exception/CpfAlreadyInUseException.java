package br.pucminas.lumen_coin_api.user.exception;

public class CpfAlreadyInUseException extends RuntimeException {
    public CpfAlreadyInUseException(String cpf) {
        super("O CPF informado já está cadastrado");
    }
}
