package br.pucminas.lumen_coin_api.benefit.controller;

import br.pucminas.lumen_coin_api.benefit.dto.request.CreateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.request.UpdateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;
import br.pucminas.lumen_coin_api.benefit.service.BenefitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final BenefitService benefitService;

    @PostMapping("/{companyId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BenefitResponse> create(
            @PathVariable UUID companyId,
            @Valid @RequestBody CreateBenefitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(benefitService.create(request, companyId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BenefitResponse>> findByCompanyId(@PathVariable UUID companyId) {
        return ResponseEntity.ok(benefitService.findByCompanyId(companyId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BenefitResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(benefitService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BenefitResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBenefitRequest request) {
        return ResponseEntity.ok(benefitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        benefitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}