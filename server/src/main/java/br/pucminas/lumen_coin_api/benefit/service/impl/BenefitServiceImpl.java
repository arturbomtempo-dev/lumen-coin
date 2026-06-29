package br.pucminas.lumen_coin_api.benefit.service.impl;

import br.pucminas.lumen_coin_api.benefit.dto.request.CreateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.request.UpdateBenefitRequest;
import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;
import br.pucminas.lumen_coin_api.benefit.entity.Benefit;
import br.pucminas.lumen_coin_api.benefit.exception.BenefitNotFoundException;
import br.pucminas.lumen_coin_api.benefit.mapper.BenefitMapper;
import br.pucminas.lumen_coin_api.benefit.repository.BenefitRepository;
import br.pucminas.lumen_coin_api.benefit.service.BenefitService;
import br.pucminas.lumen_coin_api.storage.service.StorageService;
import br.pucminas.lumen_coin_api.user.exception.CompanyNotFoundException;
import br.pucminas.lumen_coin_api.user.exception.InstitutionNotFoundException;
import br.pucminas.lumen_coin_api.user.repository.CompanyRepository;
import br.pucminas.lumen_coin_api.user.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

// Considerar uso de construtores com parâmetros e redução de verificações de null
// Utilizar construtores com parâmetros nas operações de CREATE evita repetição de código.
@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final BenefitRepository benefitRepository;
    private final CompanyRepository companyRepository;
    private final InstitutionRepository institutionRepository;
    private final BenefitMapper mapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public BenefitResponse create(CreateBenefitRequest request, UUID companyId, MultipartFile image) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException(companyId);
        }

        String imageUrl = storageService.upload(image);

        /* Benefit benefit = new Benefit(companyId, req.name(), req.description(), imageUrl, req.cost()); */
        Benefit benefit = new Benefit();
        benefit.setCompanyId(companyId);
        benefit.setName(request.name());
        benefit.setDescription(request.description());
        benefit.setImageUrl(imageUrl);
        benefit.setCost(request.cost());

        return mapper.toResponse(benefitRepository.save(benefit));
    }

    @Override
    @Transactional
    public BenefitResponse createForInstitution(CreateBenefitRequest request, UUID institutionId, MultipartFile image) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new InstitutionNotFoundException(institutionId);
        }

        String imageUrl = storageService.upload(image);

        /* Benefit benefit = new Benefit(institutionId, req.name(), req.description(), imageUrl, req.cost()); */
        Benefit benefit = new Benefit();
        benefit.setInstitutionId(institutionId);
        benefit.setName(request.name());
        benefit.setDescription(request.description());
        benefit.setImageUrl(imageUrl);
        benefit.setCost(request.cost());

        return mapper.toResponse(benefitRepository.save(benefit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> findByCompanyId(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException(companyId);
        }

        return benefitRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> findByInstitutionId(UUID institutionId) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new InstitutionNotFoundException(institutionId);
        }

        return benefitRepository.findByInstitutionId(institutionId)
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
    public BenefitResponse update(UUID id, UpdateBenefitRequest request, MultipartFile image) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new BenefitNotFoundException(id));

        if (request.name() != null) {
            benefit.setName(request.name());
        }
        if (request.description() != null) {
            benefit.setDescription(request.description());
        }
        if (image != null && !image.isEmpty()) {
            benefit.setImageUrl(storageService.upload(image));
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
}
