package br.pucminas.lumen_coin_api.course.mapper;

import br.pucminas.lumen_coin_api.course.dto.response.CourseResponse;
import br.pucminas.lumen_coin_api.course.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapperImpl implements CourseMapper {

    @Override
    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getShift(),
                course.getPeriods(),
                course.getInstitutionId(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
