package br.pucminas.lumen_coin_api.benefit_redemption.exception;

public class RedemptionAlreadyUsedException extends RuntimeException {

    public RedemptionAlreadyUsedException() {
        super("Este cupom já foi utilizado.");
    }
}
