package br.pucminas.lumen_coin_api.user.controller;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterCompanyRequest;
import br.pucminas.lumen_coin_api.user.dto.response.CompanyResponse;
import br.pucminas.lumen_coin_api.user.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> register(
            @Valid @RequestBody RegisterCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.register(request));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> findAll() {
        return ResponseEntity.ok(companyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.findById(id));
    }
}
