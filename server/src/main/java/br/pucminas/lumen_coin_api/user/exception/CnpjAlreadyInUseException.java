package br.pucminas.lumen_coin_api.user.exception;

public class CnpjAlreadyInUseException extends RuntimeException {
    public CnpjAlreadyInUseException(String cnpj) {
        super("O CNPJ informado já está cadastrado");
    }
}
