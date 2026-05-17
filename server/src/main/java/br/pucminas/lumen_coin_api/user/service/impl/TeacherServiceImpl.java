package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;
import br.pucminas.lumen_coin_api.user.entity.Teacher;
import br.pucminas.lumen_coin_api.user.exception.CpfAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
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

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public TeacherResponse register(RegisterTeacherRequest request, UUID institutionId) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (teacherRepository.existsByCpf(request.cpf())) {
            throw new CpfAlreadyInUseException(request.cpf());
        }

        Teacher teacher = new Teacher();
        teacher.setName(request.name());
        teacher.setEmail(request.email());
        teacher.setPassword(passwordEncoder.encode(request.password()));
        teacher.setAvatar(request.avatar());
        teacher.setCpf(request.cpf());
        teacher.setDepartment(request.department());
        teacher.setInstitutionId(institutionId);

        Teacher saved = teacherRepository.save(teacher);
        emailService.sendWelcome(saved.getEmail(), saved.getName());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> findAll() {
        return teacherRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse findById(UUID id) {
        return teacherRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

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

        return mapper.toResponse(teacherRepository.save(teacher));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        teacherRepository.delete(teacher);
    }
}
