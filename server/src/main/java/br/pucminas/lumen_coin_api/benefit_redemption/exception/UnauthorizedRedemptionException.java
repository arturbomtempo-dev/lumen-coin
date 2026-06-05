package br.pucminas.lumen_coin_api.benefit_redemption.exception;

public class UnauthorizedRedemptionException extends RuntimeException {

    public UnauthorizedRedemptionException() {
        super("Este cupom não pertence à sua empresa.");
    }
}
