package br.pucminas.lumen_coin_api.benefit.service.impl;

import br.pucminas.lumen_coin_api.benefit.dto.request.CreateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.request.UpdateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;
import br.pucminas.lumen_coin_api.benefit.entity.Benefit;
import br.pucminas.lumen_coin_api.benefit.exception.BenefitNotFoundException;
import br.pucminas.lumen_coin_api.benefit.mapper.BenefitMapper;
import br.pucminas.lumen_coin_api.benefit.repository.BenefitRepository;
import br.pucminas.lumen_coin_api.benefit.service.BenefitService;
import br.pucminas.lumen_coin_api.user.exception.CompanyNotFoundException;
import br.pucminas.lumen_coin_api.user.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final BenefitRepository benefitRepository;
    private final CompanyRepository companyRepository;
    private final BenefitMapper mapper;

    @Override
    @Transactional
    public BenefitResponse create(CreateBenefitRequest request, UUID companyId) {
        ensureCompanyExists(companyId);

        Benefit benefit = new Benefit();
        benefit.setCompanyId(companyId);
        benefit.setName(request.name());
        benefit.setDescription(request.description());
        benefit.setImageUrl(request.imageUrl());
        benefit.setCost(request.cost());

        return mapper.toResponse(benefitRepository.save(benefit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> findByCompanyId(UUID companyId) {
        ensureCompanyExists(companyId);

        return benefitRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BenefitResponse findById(UUID id) {
        return benefitRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BenefitNotFoundException(id));
    }

    @Override
    @Transactional
    public BenefitResponse update(UUID id, UpdateBenefitRequest request) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new BenefitNotFoundException(id));

        if (request.name() != null) {
            benefit.setName(request.name());
        }
        if (request.description() != null) {
            benefit.setDescription(request.description());
        }
        if (request.imageUrl() != null) {
            benefit.setImageUrl(request.imageUrl());
        }
        if (request.cost() != null) {
            benefit.setCost(request.cost());
        }

        return mapper.toResponse(benefitRepository.save(benefit));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new BenefitNotFoundException(id));

        benefitRepository.delete(benefit);
    }

    private void ensureCompanyExists(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException(companyId);
        }
    }
}