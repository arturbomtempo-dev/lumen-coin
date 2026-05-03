package br.pucminas.lumen_coin_api.user.controller;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;
import br.pucminas.lumen_coin_api.user.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/institutions")
@RequiredArgsConstructor
public class InstitutionController {
    private final InstitutionService institutionService;

    @PostMapping
    public ResponseEntity<InstitutionResponse> register(
            @Valid @RequestBody RegisterInstitutionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(institutionService.register(request));
    }

    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> findAll() {
        return ResponseEntity.ok(institutionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(institutionService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<InstitutionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInstitutionRequest request) {
        return ResponseEntity.ok(institutionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<Void> softDelete(@PathVariable UUID id) {
        institutionService.softDelete(id);
        return ResponseEntity.ok().build();
    }
}
