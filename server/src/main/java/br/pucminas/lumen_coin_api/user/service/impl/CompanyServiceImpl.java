package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterCompanyRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateCompanyRequest;
import br.pucminas.lumen_coin_api.user.dto.response.CompanyResponse;
import br.pucminas.lumen_coin_api.user.entity.Company;
import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.exception.CnpjAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.mapper.UserMapper;
import br.pucminas.lumen_coin_api.user.repository.CompanyRepository;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import br.pucminas.lumen_coin_api.user.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public CompanyResponse register(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (companyRepository.existsByCnpj(request.cnpj())) {
            throw new CnpjAlreadyInUseException(request.cnpj());
        }

        Company company = new Company();
        company.setName(request.name());
        company.setEmail(request.email());
        company.setPassword(passwordEncoder.encode(request.password()));
        company.setAvatar(request.avatar() != null ? request.avatar() : Avatar.COMPANY);
        company.setCnpj(request.cnpj());

        Company saved = companyRepository.save(company);
        emailService.sendWelcome(saved.getEmail(), saved.getName());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        return companyRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    public CompanyResponse update(UUID id, UpdateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.name() != null)
            company.setName(request.name());

        if (request.email() != null && !request.email().equals(company.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyInUseException(request.email());
            }
            company.setEmail(request.email());
        }

        if (request.password() != null) {
            company.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.avatar() != null)
            company.setAvatar(request.avatar());

        if (request.cnpj() != null && !request.cnpj().equals(company.getCnpj())) {
            if (companyRepository.existsByCnpj(request.cnpj())) {
                throw new CnpjAlreadyInUseException(request.cnpj());
            }
            company.setCnpj(request.cnpj());
        }

        return mapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        companyRepository.delete(company);
    }
}
