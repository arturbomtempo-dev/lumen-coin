package br.pucminas.lumen_coin_api.user.repository;

import br.pucminas.lumen_coin_api.user.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    boolean existsByCnpj(String cnpj);

    Optional<Company> findByCnpj(String cnpj);
}
