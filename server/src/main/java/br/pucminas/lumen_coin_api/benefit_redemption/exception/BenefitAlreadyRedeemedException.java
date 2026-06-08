package br.pucminas.lumen_coin_api.benefit_redemption.exception;

public class BenefitAlreadyRedeemedException extends RuntimeException {

    public BenefitAlreadyRedeemedException() {
        super("Esta vantagem já foi solicitada e não está mais disponível para resgate.");
    }
}
