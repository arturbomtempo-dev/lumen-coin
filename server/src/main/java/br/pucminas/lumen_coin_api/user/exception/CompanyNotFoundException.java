package br.pucminas.lumen_coin_api.user.exception;

import java.util.UUID;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(UUID id) {
        super("Company not found");
    }
}