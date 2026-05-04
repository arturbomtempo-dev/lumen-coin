package br.pucminas.lumen_coin_api.course.mapper;

import br.pucminas.lumen_coin_api.course.dto.response.CourseResponse;
import br.pucminas.lumen_coin_api.course.entity.Course;

public interface CourseMapper {
    CourseResponse toResponse(Course course);
}
