package br.pucminas.lumen_coin_api.user.service;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;

import java.util.List;
import java.util.UUID;

public interface TeacherService {
    TeacherResponse register(RegisterTeacherRequest request, UUID institutionId);

    List<TeacherResponse> findAll();

    TeacherResponse findById(UUID id);

    TeacherResponse update(UUID id, UpdateTeacherRequest request);

    void delete(UUID id);
}
