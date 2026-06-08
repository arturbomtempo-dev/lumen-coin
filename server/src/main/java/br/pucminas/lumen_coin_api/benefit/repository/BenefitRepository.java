package br.pucminas.lumen_coin_api.benefit.repository;

import br.pucminas.lumen_coin_api.benefit.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, UUID> {
    List<Benefit> findByCompanyId(UUID companyId);

    List<Benefit> findByInstitutionId(UUID institutionId);
}