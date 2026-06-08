package br.pucminas.lumen_coin_api.benefit_redemption.service;

import br.pucminas.lumen_coin_api.benefit_redemption.dto.request.CreateBenefitRedemptionRequest;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.BenefitRedemptionResponse;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.ValidateBenefitRedemptionResponse;

import java.util.List;
import java.util.UUID;

public interface BenefitRedemptionService {

    BenefitRedemptionResponse redeem(UUID studentId, CreateBenefitRedemptionRequest request);

    List<BenefitRedemptionResponse> getMyRedemptions(UUID studentId);

    List<BenefitRedemptionResponse> getCompanyRedemptions(UUID companyId);

    ValidateBenefitRedemptionResponse validateCoupon(String couponCode, UUID companyId);

    BenefitRedemptionResponse markAsUsed(String couponCode, UUID companyId, String usageNotes);
}
