package br.pucminas.lumen_coin_api.user.service;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.response.StudentResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    StudentResponse register(RegisterStudentRequest request);

    List<StudentResponse> findAll();

    StudentResponse findById(UUID id);

    StudentResponse update(UUID id, UpdateStudentRequest request);

    void softDelete(UUID id);
}
