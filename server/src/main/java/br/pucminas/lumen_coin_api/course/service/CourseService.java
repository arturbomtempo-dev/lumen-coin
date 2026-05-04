package br.pucminas.lumen_coin_api.course.service;

import br.pucminas.lumen_coin_api.course.dto.request.RegisterCourseRequest;
import br.pucminas.lumen_coin_api.course.dto.request.UpdateCourseRequest;
import br.pucminas.lumen_coin_api.course.dto.response.CourseResponse;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseResponse register(RegisterCourseRequest request, UUID institutionId);

    List<CourseResponse> findAll();

    CourseResponse findById(UUID id);

    CourseResponse update(UUID id, UpdateCourseRequest request);

    void delete(UUID id);
}
