package br.pucminas.lumen_coin_api.user.exception;

import java.util.UUID;

public class InstitutionNotFoundException extends RuntimeException {
    public InstitutionNotFoundException(UUID id) {
        super("Institution not found");
    }
}
