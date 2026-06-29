package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.common.util.PasswordGenerator;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.whatsapp.service.WhatsAppService;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeInitialPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeTeacherPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;
import br.pucminas.lumen_coin_api.user.entity.Teacher;
import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.exception.CpfAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.IncorrectPasswordException;
import br.pucminas.lumen_coin_api.user.exception.PasswordMismatchException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.mapper.UserMapper;
import br.pucminas.lumen_coin_api.user.repository.TeacherRepository;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import br.pucminas.lumen_coin_api.user.service.TeacherService;
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
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    /// Sugestões:
    /// - Utilizar um construtor com parâmetros de Teacher para criar o professor
    /// deixaria o código mais legível e diminuiria um pouco o número de linhas.
    /// - Mudar o nome do DTO para identificá-lo como um também aumentaria a legibilidade.
    /// - Mudar o nome do objeto "request" para "req" teria o mesmo efeito ao ser usado dentro do construtor de Teacher
    /// - Criar uma variável para a senha criptografada também.
    @Override
    @Transactional
    public TeacherResponse register(/* RegisterTeacherRequestDTO req */ RegisterTeacherRequest request, UUID institutionId) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (teacherRepository.existsByCpf(request.cpf())) {
            throw new CpfAlreadyInUseException(request.cpf());
        }

        // String encodedPswd = passwordEncoder.encode(PasswordGenerator.generate())
        String generatedPassword = PasswordGenerator.generate();

        /* Teacher teacher = new Teacher(req.name(), req.email(), encodedPswd, req.cpf(), req.department(), req.institutionId(),
        * req.phone()); */

        Teacher teacher = new Teacher();
        teacher.setName(request.name());
        teacher.setEmail(request.email());
        teacher.setPassword(passwordEncoder.encode(generatedPassword));
        /// Parece que o tratamenro do avatar nulo varia de acordo com o tipo de usuário. Caso essa pré definição
        /// de avatar não seja importante, seria bom padronizar.
        teacher.setAvatar(Avatar.MARIO);
        teacher.setCpf(request.cpf());
        teacher.setDepartment(request.department());
        teacher.setInstitutionId(institutionId);
        teacher.setPhone(request.phone());
        // Uso OK de setter.
        teacher.setBalance(1000);

        Teacher saved = teacherRepository.save(teacher);
        emailService.sendTeacherWelcome(saved.getEmail(), saved.getName(), generatedPassword);
        whatsAppService.sendTeacherWelcome(saved.getPhone(), saved.getName());
        return mapper.toResponse(saved);
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> findAll() {
        return teacherRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public TeacherResponse findById(UUID id) {
        return teacherRepository.findById(id)
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
    public TeacherResponse update(UUID id, UpdateTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.name() != null)
            teacher.setName(request.name());

        if (request.email() != null && !request.email().equals(teacher.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyInUseException(request.email());
            }
            teacher.setEmail(request.email());
        }

        if (request.password() != null) {
            teacher.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.avatar() != null)
            teacher.setAvatar(request.avatar());

        if (request.cpf() != null && !request.cpf().equals(teacher.getCpf())) {
            if (teacherRepository.existsByCpf(request.cpf())) {
                throw new CpfAlreadyInUseException(request.cpf());
            }
            teacher.setCpf(request.cpf());
        }

        if (request.department() != null)
            teacher.setDepartment(request.department());

        if (request.phone() != null)
            teacher.setPhone(request.phone());

        return mapper.toResponse(teacherRepository.save(teacher));
    }

    // OK
    @Override
    @Transactional
    public void changePassword(UUID id, ChangeTeacherPasswordRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.currentPassword(), teacher.getPassword())) {
            throw new IncorrectPasswordException();
        }
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordMismatchException();
        }

        teacher.setPassword(passwordEncoder.encode(request.newPassword()));
        teacherRepository.save(teacher);
    }

    // OK
    @Override
    @Transactional
    public void changeInitialPassword(UUID id, ChangeInitialPasswordRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordMismatchException();
        }

        teacher.setPassword(passwordEncoder.encode(request.newPassword()));
        teacher.setFirstLogin(false);
        teacherRepository.save(teacher);
    }

    // OK
    @Override
    @Transactional
    public void delete(UUID id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        teacherRepository.delete(teacher);
    }
}
