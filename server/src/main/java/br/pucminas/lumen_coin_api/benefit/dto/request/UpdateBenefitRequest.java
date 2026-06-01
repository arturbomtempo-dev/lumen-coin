package br.pucminas.lumen_coin_api.benefit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateBenefitRequest(
        @Size(max = 150, message = "Name must not exceed 150 characters") String name,
        @Size(max = 2000, message = "Description must not exceed 2000 characters") String description,
        @Size(max = 500, message = "Image URL must not exceed 500 characters") String imageUrl,
        @Min(value = 1, message = "Cost must be at least 1") Integer cost) {
}