package br.pucminas.lumen_coin_api.benefit_redemption.repository;

import br.pucminas.lumen_coin_api.benefit_redemption.entity.BenefitRedemption;
import br.pucminas.lumen_coin_api.benefit_redemption.enums.RedemptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface BenefitRedemptionRepository extends JpaRepository<BenefitRedemption, UUID> {

    List<BenefitRedemption> findByStudentIdOrderByRedeemedAtDesc(UUID studentId);

    List<BenefitRedemption> findByCompanyIdOrderByRedeemedAtDesc(UUID companyId);

    boolean existsByCouponCode(String couponCode);

    boolean existsByBenefitId(UUID benefitId);

    @Query("SELECT r.benefitId FROM BenefitRedemption r WHERE r.status = :status")
    Set<UUID> findBenefitIdsByStatus(@Param("status") RedemptionStatus status);

    Optional<BenefitRedemption> findByCouponCode(String couponCode);
}
