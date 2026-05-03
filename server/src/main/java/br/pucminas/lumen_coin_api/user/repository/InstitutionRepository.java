package br.pucminas.lumen_coin_api.user.repository;

import br.pucminas.lumen_coin_api.user.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
    boolean existsByCnpj(String cnpj);

    Optional<Institution> findByCnpj(String cnpj);
}
