package br.pucminas.lumen_coin_api.benefit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBenefitRequest(
        @NotBlank(message = "Name is required") @Size(max = 150, message = "Name must not exceed 150 characters") String name,
        @NotBlank(message = "Description is required") @Size(max = 2000, message = "Description must not exceed 2000 characters") String description,
        @NotBlank(message = "Image URL is required") @Size(max = 500, message = "Image URL must not exceed 500 characters") String imageUrl,
        @NotNull(message = "Cost is required") @Min(value = 1, message = "Cost must be at least 1") Integer cost) {
}