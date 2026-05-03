package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;
import br.pucminas.lumen_coin_api.user.entity.Institution;
import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.exception.CnpjAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.mapper.UserMapper;
import br.pucminas.lumen_coin_api.user.repository.InstitutionRepository;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import br.pucminas.lumen_coin_api.user.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    @Transactional
    public InstitutionResponse register(RegisterInstitutionRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (institutionRepository.existsByCnpj(request.cnpj())) {
            throw new CnpjAlreadyInUseException(request.cnpj());
        }

        Institution institution = new Institution();
        institution.setName(request.name());
        institution.setEmail(request.email());
        institution.setPassword(passwordEncoder.encode(request.password()));
        institution.setAvatar(request.avatar() != null ? request.avatar() : Avatar.INSTITUTION);
        institution.setCnpj(request.cnpj());
        institution.setZipCode(request.zipCode());
        institution.setAddress(request.address());

        return mapper.toResponse(institutionRepository.save(institution));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstitutionResponse> findAll() {
        return institutionRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InstitutionResponse findById(UUID id) {
        return institutionRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
