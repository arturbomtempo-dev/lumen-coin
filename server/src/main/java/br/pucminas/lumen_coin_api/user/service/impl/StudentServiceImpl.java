package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.response.StudentResponse;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.exception.CpfAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.EmailAlreadyInUseException;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.mapper.UserMapper;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import br.pucminas.lumen_coin_api.user.repository.UserRepository;
import br.pucminas.lumen_coin_api.user.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    @Transactional
    public StudentResponse register(RegisterStudentRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        if (studentRepository.existsByCpf(request.cpf())) {
            throw new CpfAlreadyInUseException(request.cpf());
        }

        Student student = new Student();
        student.setName(request.name());
        student.setEmail(request.email());
        student.setPassword(passwordEncoder.encode(request.password()));
        student.setAvatar(request.avatar());
        student.setCpf(request.cpf());
        student.setRg(request.rg());
        student.setZipCode(request.zipCode());
        student.setAddress(request.address());

        return mapper.toResponse(studentRepository.save(student));
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

        return mapper.toResponse(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        studentRepository.delete(student);
    }
}
