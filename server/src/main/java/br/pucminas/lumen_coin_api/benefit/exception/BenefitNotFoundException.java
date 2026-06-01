package br.pucminas.lumen_coin_api.benefit.exception;

import java.util.UUID;

public class BenefitNotFoundException extends RuntimeException {
    public BenefitNotFoundException(UUID id) {
        super("Benefit not found");
    }
}