package br.pucminas.lumen_coin_api.benefit_redemption.entity;

import br.pucminas.lumen_coin_api.benefit_redemption.enums.RedemptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_benefit_redemptions", indexes = {
        @Index(name = "idx_redemptions_student_id", columnList = "student_id"),
        @Index(name = "idx_redemptions_company_id", columnList = "company_id"),
        @Index(name = "idx_redemptions_institution_id", columnList = "institution_id")
})
@Getter
@Setter
public class BenefitRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "benefit_id", nullable = false)
    private UUID benefitId;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "institution_id")
    private UUID institutionId;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 20)
    private String couponCode;

    @Column(name = "coins_spent", nullable = false)
    private int coinsSpent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RedemptionStatus status;

    @Column(name = "redeemed_at", nullable = false, updatable = false)
    private Instant redeemedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @PrePersist
    void prePersist() {
        this.redeemedAt = Instant.now();
        this.status = RedemptionStatus.PENDING;
    }
}
