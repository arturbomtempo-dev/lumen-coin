package br.pucminas.lumen_coin_api.coin_transfer.repository;

import br.pucminas.lumen_coin_api.coin_transfer.entity.CoinTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoinTransferRepository extends JpaRepository<CoinTransfer, UUID> {

    List<CoinTransfer> findBySenderIdOrderBySentAtDesc(UUID senderId);

    List<CoinTransfer> findByRecipientIdOrderBySentAtDesc(UUID recipientId);
}
