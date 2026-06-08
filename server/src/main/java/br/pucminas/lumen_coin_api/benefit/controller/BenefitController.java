package br.pucminas.lumen_coin_api.benefit.controller;

import br.pucminas.lumen_coin_api.benefit.dto.request.CreateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.request.UpdateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;
import br.pucminas.lumen_coin_api.benefit.service.BenefitService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final BenefitService benefitService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BenefitResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @ModelAttribute @Valid CreateBenefitRequest request,
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(benefitService.create(request, principal.getUserId(), image));
    }

    @PostMapping(value = "/institution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<BenefitResponse> createForInstitution(
            @AuthenticationPrincipal UserPrincipal principal,
            @ModelAttribute @Valid CreateBenefitRequest request,
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(benefitService.createForInstitution(request, principal.getUserId(), image));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BenefitResponse>> findByCompanyId(@PathVariable UUID companyId) {
        return ResponseEntity.ok(benefitService.findByCompanyId(companyId));
    }

    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<BenefitResponse>> findByInstitutionId(@PathVariable UUID institutionId) {
        return ResponseEntity.ok(benefitService.findByInstitutionId(institutionId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY', 'INSTITUTION')")
    public ResponseEntity<BenefitResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(benefitService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('COMPANY', 'INSTITUTION')")
    public ResponseEntity<BenefitResponse> update(
            @PathVariable UUID id,
            @ModelAttribute @Valid UpdateBenefitRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(benefitService.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY', 'INSTITUTION')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        benefitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
