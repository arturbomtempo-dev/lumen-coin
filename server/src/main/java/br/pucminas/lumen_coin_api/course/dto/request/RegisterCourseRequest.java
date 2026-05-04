package br.pucminas.lumen_coin_api.course.dto.request;

import br.pucminas.lumen_coin_api.course.enums.Shift;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterCourseRequest(
        @NotBlank(message = "Name is required") @Size(max = 150, message = "Name must not exceed 150 characters") String name,
        @NotNull(message = "Shift is required") Shift shift,
        @NotNull(message = "Periods is required") @Min(value = 1, message = "Periods must be at least 1") Integer periods) {
}
