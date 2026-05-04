package br.pucminas.lumen_coin_api.user.service;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;

import java.util.List;
import java.util.UUID;

public interface InstitutionService {
    InstitutionResponse register(RegisterInstitutionRequest request);

    List<InstitutionResponse> findAll();

    InstitutionResponse findById(UUID id);

    InstitutionResponse update(UUID id, UpdateInstitutionRequest request);

    void delete(UUID id);
}
