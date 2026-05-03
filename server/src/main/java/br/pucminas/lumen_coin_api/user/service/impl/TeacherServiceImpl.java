package br.pucminas.lumen_coin_api.user.service.impl;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterTeacherRequest;
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

    @Override
    @Transactional
    public TeacherResponse register(RegisterTeacherRequest request) {
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

        return mapper.toResponse(teacherRepository.save(teacher));
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
}
