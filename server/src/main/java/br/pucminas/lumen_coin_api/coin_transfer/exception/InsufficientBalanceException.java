package br.pucminas.lumen_coin_api.coin_transfer.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(int available, int requested) {
        super("Saldo insuficiente. Disponível: " + available + ", solicitado: " + requested + ".");
    }
}
