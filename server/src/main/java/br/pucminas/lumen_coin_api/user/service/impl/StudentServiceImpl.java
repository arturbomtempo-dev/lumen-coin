package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.course.entity.Course;
import br.pucminas.lumen_coin_api.course.exception.CourseNotFoundException;
import br.pucminas.lumen_coin_api.course.repository.CourseRepository;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.whatsapp.service.WhatsAppService;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeStudentPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.response.StudentResponse;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.exception.CpfAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.IncorrectPasswordException;
import br.pucminas.lumen_coin_api.user.exception.PasswordMismatchException;
import br.pucminas.lumen_coin_api.user.exception.StudentInstitutionCourseMismatchException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.mapper.UserMapper;
import br.pucminas.lumen_coin_api.user.repository.InstitutionRepository;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import br.pucminas.lumen_coin_api.user.service.StudentService;
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
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    /// Sugestões:
    /// - Utilizar um construtor com parâmetros de Student para criar o aluno
    /// deixaria o código mais legível e diminuiria um pouco o número de linhas.
    /// - Mudar o nome do DTO para identificá-lo como um também aumentaria a legibilidade.
    /// - Mudar o nome do objeto "request" para "req" teria o mesmo efeito ao ser usado dentro do construtor de Student
    /// - Criar uma variável para a senha criptografada também.
    @Override
    @Transactional
    public StudentResponse register(/* RegisterStudentRequestDTO req */ RegisterStudentRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (studentRepository.existsByCpf(request.cpf())) {
            throw new CpfAlreadyInUseException(request.cpf());
        }

        // Diminuindo de 10 para 3 linhas a mesma operação

        // String encodedPswd = passwordEncoder.encode(req.password());

        /* Student student = new Student(req.name(), req.email(), encodedPswd, req.cpf(), req.rg(), req.zipCode(),
        req.address(), req.phone())*/

        Student student = new Student();
        student.setName(request.name());
        student.setEmail(request.email());
        student.setPassword(passwordEncoder.encode(request.password()));
        // Possível erro: verificação de avatar nulo não está sendo feita para a criação de alunos.
        // student.setAvatar(request.avatar() != null ? request.avatar() : Avatar.INSTITUTION);
        student.setAvatar(request.avatar());
        student.setCpf(request.cpf());
        student.setRg(request.rg());
        student.setZipCode(request.zipCode());
        student.setAddress(request.address());
        student.setPhone(request.phone());

        if (!institutionRepository.existsById(request.institutionId())) {
            throw new UserNotFoundException(request.institutionId());
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CourseNotFoundException(request.courseId()));

        if (!course.getInstitutionId().equals(request.institutionId())) {
            throw new StudentInstitutionCourseMismatchException(request.institutionId(), request.courseId());
        }

        // O uso dos setters realmente é a melhor opção nesse caso.
        student.setInstitutionId(request.institutionId());
        student.setCourseId(request.courseId());

        Student saved = studentRepository.save(student);
        emailService.sendWelcome(saved.getEmail(), saved.getName());
        whatsAppService.sendWelcome(saved.getPhone(), saved.getName());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse findById(UUID id) {
        return studentRepository.findById(id)
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
    public StudentResponse update(UUID id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.name() != null)
            student.setName(request.name());

        if (request.email() != null && !request.email().equals(student.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyInUseException(request.email());
            }
            student.setEmail(request.email());
        }

        if (request.password() != null) {
            student.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.avatar() != null)
            student.setAvatar(request.avatar());

        if (request.cpf() != null && !request.cpf().equals(student.getCpf())) {
            if (studentRepository.existsByCpf(request.cpf())) {
                throw new CpfAlreadyInUseException(request.cpf());
            }
            student.setCpf(request.cpf());
        }

        if (request.rg() != null)
            student.setRg(request.rg());
        if (request.zipCode() != null)
            student.setZipCode(request.zipCode());
        if (request.address() != null)
            student.setAddress(request.address());

        if (request.phone() != null)
            student.setPhone(request.phone());

        if (request.institutionId() != null) {
            if (!institutionRepository.existsById(request.institutionId())) {
                throw new UserNotFoundException(request.institutionId());
            }
            student.setInstitutionId(request.institutionId());
        }

        if (request.courseId() != null) {
            Course requestCourse = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new CourseNotFoundException(request.courseId()));
            student.setCourseId(requestCourse.getId());
        }

        UUID targetInstitutionId = student.getInstitutionId();
        UUID targetCourseId = student.getCourseId();

        if (targetCourseId != null) {
            Course targetCourse = courseRepository.findById(targetCourseId)
                    .orElseThrow(() -> new CourseNotFoundException(targetCourseId));

            if (targetInstitutionId == null || !targetCourse.getInstitutionId().equals(targetInstitutionId)) {
                throw new StudentInstitutionCourseMismatchException(targetInstitutionId, targetCourseId);
            }
        }

        return mapper.toResponse(studentRepository.save(student));
    }

    // OK
    @Override
    @Transactional
    public void changePassword(UUID id, ChangeStudentPasswordRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.currentPassword(), student.getPassword())) {
            throw new IncorrectPasswordException();
        }
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordMismatchException();
        }

        student.setPassword(passwordEncoder.encode(request.newPassword()));
        studentRepository.save(student);
    }

    // OK
    @Override
    @Transactional
    public void delete(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        studentRepository.delete(student);
    }

    // OK
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findByInstitutionId(UUID institutionId) {
        return studentRepository.findByInstitutionId(institutionId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
