package br.pucminas.lumen_coin_api.course.dto.request;

import br.pucminas.lumen_coin_api.course.enums.Shift;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateCourseRequest(
        @Size(max = 150, message = "Name must not exceed 150 characters") String name,
        Shift shift,
        @Min(value = 1, message = "Periods must be at least 1") Integer periods) {
}
