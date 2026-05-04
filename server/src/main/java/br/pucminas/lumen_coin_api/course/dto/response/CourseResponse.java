package br.pucminas.lumen_coin_api.course.dto.response;

import br.pucminas.lumen_coin_api.course.enums.Shift;

import java.time.Instant;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String name,
        Shift shift,
        int periods,
        UUID institutionId,
        Instant createdAt,
        Instant updatedAt) {
}
