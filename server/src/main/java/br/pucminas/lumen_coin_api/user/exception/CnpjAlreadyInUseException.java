package br.pucminas.lumen_coin_api.user.exception;

public class CnpjAlreadyInUseException extends RuntimeException {
    public CnpjAlreadyInUseException(String cnpj) {
        super("The CNPJ '" + cnpj + "' is already registered");
    }
}
