package br.pucminas.lumen_coin_api.coin_transfer.service;

import br.pucminas.lumen_coin_api.coin_transfer.dto.request.SendCoinTransferRequest;
import br.pucminas.lumen_coin_api.coin_transfer.dto.response.CoinTransferResponse;

import java.util.List;
import java.util.UUID;

public interface CoinTransferService {

    CoinTransferResponse send(UUID teacherId, SendCoinTransferRequest request);

    List<CoinTransferResponse> getSentByTeacher(UUID teacherId);

    List<CoinTransferResponse> getReceivedByStudent(UUID studentId);
}
