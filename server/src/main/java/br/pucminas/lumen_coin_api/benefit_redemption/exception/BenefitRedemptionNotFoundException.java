package br.pucminas.lumen_coin_api.benefit_redemption.exception;

import java.util.UUID;

public class BenefitRedemptionNotFoundException extends RuntimeException {

    public BenefitRedemptionNotFoundException(UUID id) {
        super("Resgate não encontrado: " + id);
    }

    public BenefitRedemptionNotFoundException(String couponCode) {
        super("Cupom não encontrado: " + couponCode);
    }
}
