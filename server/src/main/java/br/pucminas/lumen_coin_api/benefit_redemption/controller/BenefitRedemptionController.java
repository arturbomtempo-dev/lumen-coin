package br.pucminas.lumen_coin_api.benefit_redemption.controller;

import br.pucminas.lumen_coin_api.benefit_redemption.dto.request.CreateBenefitRedemptionRequest;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.request.DenyBenefitRedemptionRequest;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.request.ScanBenefitRedemptionRequest;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.request.UseBenefitRedemptionRequest;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.BenefitRedemptionResponse;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.RedeemedBenefitIdsResponse;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.ValidateBenefitRedemptionResponse;
import br.pucminas.lumen_coin_api.benefit_redemption.service.BenefitRedemptionService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/benefit-redemptions")
@RequiredArgsConstructor
public class BenefitRedemptionController {

    private final BenefitRedemptionService benefitRedemptionService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<BenefitRedemptionResponse> redeem(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateBenefitRedemptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(benefitRedemptionService.redeem(principal.getUserId(), request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<BenefitRedemptionResponse>> getMyRedemptions(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(benefitRedemptionService.getMyRedemptions(principal.getUserId()));
    }

    @GetMapping("/redeemed-benefit-ids")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMPANY', 'INSTITUTION')")
    public ResponseEntity<RedeemedBenefitIdsResponse> getRedeemedBenefitIds() {
        return ResponseEntity.ok(benefitRedemptionService.getRedeemedBenefitIds());
    }

    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<BenefitRedemptionResponse>> getCompanyRedemptions(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(benefitRedemptionService.getCompanyRedemptions(principal.getUserId()));
    }

    @GetMapping("/institution")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<List<BenefitRedemptionResponse>> getInstitutionRedemptions(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(benefitRedemptionService.getInstitutionRedemptions(principal.getUserId()));
    }

    @GetMapping("/validate/{couponCode}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ValidateBenefitRedemptionResponse> validateCoupon(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String couponCode) {
        return ResponseEntity.ok(benefitRedemptionService.validateCoupon(couponCode, principal.getUserId()));
    }

    @GetMapping("/validate-institution/{couponCode}")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<ValidateBenefitRedemptionResponse> validateCouponForInstitution(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String couponCode) {
        return ResponseEntity.ok(benefitRedemptionService.validateCouponForInstitution(couponCode, principal.getUserId()));
    }

    @PatchMapping("/use")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BenefitRedemptionResponse> markAsUsed(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UseBenefitRedemptionRequest request) {
        return ResponseEntity.ok(benefitRedemptionService.markAsUsed(
                request.couponCode(), principal.getUserId(), request.usageNotes()));
    }

    @PatchMapping("/use-institution")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<BenefitRedemptionResponse> markAsUsedByInstitution(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UseBenefitRedemptionRequest request) {
        return ResponseEntity.ok(benefitRedemptionService.markAsUsedByInstitution(
                request.couponCode(), principal.getUserId(), request.usageNotes()));
    }

    @PatchMapping("/scan")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BenefitRedemptionResponse> redeemByScan(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ScanBenefitRedemptionRequest request) {
        return ResponseEntity.ok(benefitRedemptionService.redeemByScan(
                request.couponCode(), principal.getUserId()));
    }

    @PatchMapping("/deny")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> denyRedemption(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DenyBenefitRedemptionRequest request) {
        benefitRedemptionService.denyRedemption(
                request.couponCode(), principal.getUserId(), request.denialReason());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deny-institution")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<Void> denyRedemptionByInstitution(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DenyBenefitRedemptionRequest request) {
        benefitRedemptionService.denyRedemptionByInstitution(
                request.couponCode(), principal.getUserId(), request.denialReason());
        return ResponseEntity.noContent().build();
    }
}
