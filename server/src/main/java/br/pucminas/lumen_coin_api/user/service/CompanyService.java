package br.pucminas.lumen_coin_api.user.service;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterCompanyRequest;
import br.pucminas.lumen_coin_api.user.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyResponse register(RegisterCompanyRequest request);

    List<CompanyResponse> findAll();

    CompanyResponse findById(UUID id);
}
