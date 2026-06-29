package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeCompanyPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterCompanyRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateCompanyRequest;
import br.pucminas.lumen_coin_api.user.dto.response.CompanyResponse;
import br.pucminas.lumen_coin_api.user.entity.Company;
import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.exception.CnpjAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.IncorrectPasswordException;
import br.pucminas.lumen_coin_api.user.exception.PasswordMismatchException;
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

/**
 * Service OK. Só alguns pequenos detalhes poderiam melhorar (veja os comentários).
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final EmailService emailService;

    /// Sugestões:
    /// - Utilizar um construtor com parâmetros de Company para criar a empresa
    /// deixaria o código mais legível e diminuiria um pouco o número de linhas.
    /// - Mudar o nome do DTO para identificá-lo como um também aumentaria a legibilidade.
    /// - Mudar o nome do objeto "request" para "req" teria o mesmo efeito ao ser usado dentro do construtor de Company
    /// - Criar uma variável para a senha criptografada também.
    @Override
    @Transactional
    public CompanyResponse register(/* RegisterCompanyRequestDTO req */ RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (companyRepository.existsByCnpj(request.cnpj())) {
            throw new CnpjAlreadyInUseException(request.cnpj());
        }

        // String encodedPswd = passwordEncoder.encode(req.password());
        /* Company comparny = new Company(req.name(), req.email(), encodedPswd, req.cnpj());*/

        Company company = new Company();
        company.setName(request.name());
        company.setEmail(request.email());
        company.setPassword(passwordEncoder.encode(request.password()));
        // Uso de setter OK
        company.setAvatar(request.avatar() != null ? request.avatar() : Avatar.COMPANY);
        company.setCnpj(request.cnpj());

        Company saved = companyRepository.save(company);
        emailService.sendWelcome(saved.getEmail(), saved.getName());
        return mapper.toResponse(saved);
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        return companyRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /// Sugestões:
    /// - Verificações de atributos obrigatórios nulos poderiam ser eliminadas
    /// se houvesse um tratamento dessas situações na criação da request ou mesmo no front-end. Isso aumentaria a legibilidade
    /// e diminuiria o número de linhas, além de aumentar a coesão do métod0 update ao tirar dele um grande
    /// número de tarefas envolvendo verificação de null
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

    // OK
    @Override
    @Transactional
    public void changePassword(UUID id, ChangeCompanyPasswordRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.currentPassword(), company.getPassword())) {
            throw new IncorrectPasswordException();
        }
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordMismatchException();
        }

        company.setPassword(passwordEncoder.encode(request.newPassword()));
        companyRepository.save(company);
    }

    // OK
    @Override
    @Transactional
    public void delete(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        companyRepository.delete(company);
    }
}
