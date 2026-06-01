package br.pucminas.lumen_coin_api.benefit.service;

import br.pucminas.lumen_coin_api.benefit.dto.request.CreateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.request.UpdateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;

import java.util.List;
import java.util.UUID;

public interface BenefitService {
    BenefitResponse create(CreateBenefitRequest request, UUID companyId);

    List<BenefitResponse> findByCompanyId(UUID companyId);

    BenefitResponse findById(UUID id);

    BenefitResponse update(UUID id, UpdateBenefitRequest request);

    void delete(UUID id);
}