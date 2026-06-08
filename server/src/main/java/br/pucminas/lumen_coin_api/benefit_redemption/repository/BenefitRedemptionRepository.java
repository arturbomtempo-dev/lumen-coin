package br.pucminas.lumen_coin_api.benefit_redemption.repository;

import br.pucminas.lumen_coin_api.benefit_redemption.entity.BenefitRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenefitRedemptionRepository extends JpaRepository<BenefitRedemption, UUID> {

    List<BenefitRedemption> findByStudentIdOrderByRedeemedAtDesc(UUID studentId);

    List<BenefitRedemption> findByCompanyIdOrderByRedeemedAtDesc(UUID companyId);

    boolean existsByCouponCode(String couponCode);

    Optional<BenefitRedemption> findByCouponCode(String couponCode);
}
