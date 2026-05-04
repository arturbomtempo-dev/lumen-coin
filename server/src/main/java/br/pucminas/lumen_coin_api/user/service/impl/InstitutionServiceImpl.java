package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.course.entity.Course;
import br.pucminas.lumen_coin_api.course.repository.CourseRepository;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateInstitutionRequest;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;
import br.pucminas.lumen_coin_api.user.entity.Institution;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.entity.Teacher;
import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.exception.CnpjAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
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
