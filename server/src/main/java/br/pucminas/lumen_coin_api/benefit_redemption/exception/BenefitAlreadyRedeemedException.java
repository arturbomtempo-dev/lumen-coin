package br.pucminas.lumen_coin_api.benefit_redemption.exception;

public class BenefitAlreadyRedeemedException extends RuntimeException {

    public BenefitAlreadyRedeemedException() {
        super("Você já solicitou esta vantagem anteriormente.");
    }
}
