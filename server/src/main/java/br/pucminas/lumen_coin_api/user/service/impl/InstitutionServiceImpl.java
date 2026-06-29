package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.course.entity.Course;
import br.pucminas.lumen_coin_api.course.repository.CourseRepository;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeInstitutionPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;
import br.pucminas.lumen_coin_api.user.entity.Institution;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.entity.Teacher;
import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.exception.CnpjAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.IncorrectPasswordException;
import br.pucminas.lumen_coin_api.user.exception.PasswordMismatchException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.mapper.UserMapper;
import br.pucminas.lumen_coin_api.user.repository.InstitutionRepository;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import br.pucminas.lumen_coin_api.user.repository.TeacherRepository;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import br.pucminas.lumen_coin_api.user.service.InstitutionService;
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
public class InstitutionServiceImpl implements InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final EmailService emailService;

    /// Sugestões:
    /// - Utilizar um construtor com parâmetros de Institution para criar a instituição
    /// deixaria o código mais legível e diminuiria um pouco o número de linhas.
    /// - Mudar o nome do DTO para identificá-lo como um também aumentaria a legibilidade.
    /// - Mudar o nome do objeto "request" para "req" teria o mesmo efeito ao ser usado dentro do construtor de Institution
    /// - Criar uma variável para a senha criptografada também.
    @Override
    @Transactional
    public InstitutionResponse register(/* RegisterInstitutionRequestDTO req */ RegisterInstitutionRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (institutionRepository.existsByCnpj(request.cnpj())) {
            throw new CnpjAlreadyInUseException(request.cnpj());
        }

        // Diminuindo de 8 para 2 linhas a mesma operação

        // String encodedPswd = passwordEncoder.encode(req.password());
        /* Institution institution = new Institution(req.name(), req.email(), encodedPswd, req.cnpj, req.zipCode, req.address()); */

        Institution institution = new Institution();
        institution.setName(request.name());
        institution.setEmail(request.email());
        institution.setPassword(passwordEncoder.encode(request.password()));
        // Uso do setter é a realmente a melhor opção aqui.
        institution.setAvatar(request.avatar() != null ? request.avatar() : Avatar.INSTITUTION);
        institution.setCnpj(request.cnpj());
        institution.setZipCode(request.zipCode());
        institution.setAddress(request.address());

        Institution saved = institutionRepository.save(institution);
        emailService.sendWelcome(saved.getEmail(), saved.getName());
        return mapper.toResponse(saved);
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public List<InstitutionResponse> findAll() {
        return institutionRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public InstitutionResponse findById(UUID id) {
        return institutionRepository.findById(id)
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
    public InstitutionResponse update(UUID id, UpdateInstitutionRequest request) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.name() != null)
            institution.setName(request.name());

        if (request.email() != null && !request.email().equals(institution.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyInUseException(request.email());
            }
            institution.setEmail(request.email());
        }

        if (request.password() != null) {
            institution.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.avatar() != null)
            institution.setAvatar(request.avatar());

        if (request.cnpj() != null && !request.cnpj().equals(institution.getCnpj())) {
            if (institutionRepository.existsByCnpj(request.cnpj())) {
                throw new CnpjAlreadyInUseException(request.cnpj());
            }
            institution.setCnpj(request.cnpj());
        }

        if (request.zipCode() != null)
            institution.setZipCode(request.zipCode());
        if (request.address() != null)
            institution.setAddress(request.address());

        return mapper.toResponse(institutionRepository.save(institution));
    }

    // OK
    @Override
    @Transactional
    public void changePassword(UUID id, ChangeInstitutionPasswordRequest request) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.currentPassword(), institution.getPassword())) {
            throw new IncorrectPasswordException();
        }
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordMismatchException();
        }

        institution.setPassword(passwordEncoder.encode(request.newPassword()));
        institutionRepository.save(institution);
    }

    // OK
    @Override
    @Transactional
    public void delete(UUID id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<Teacher> teachers = teacherRepository.findByInstitutionId(id);
        teacherRepository.deleteAll(teachers);

        List<Student> students = studentRepository.findByInstitutionId(id);
        students.forEach(student -> {
            student.setInstitutionId(null);
            student.setCourseId(null);
        });
        studentRepository.saveAll(students);

        List<Course> courses = courseRepository.findByInstitutionId(id);
        courseRepository.deleteAll(courses);

        institutionRepository.delete(institution);
    }
}
