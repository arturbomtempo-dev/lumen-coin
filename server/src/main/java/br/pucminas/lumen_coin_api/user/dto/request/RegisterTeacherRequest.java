package br.pucminas.lumen_coin_api.user.dto.request;

import jakarta.validation.constraints.*;

public record RegisterTeacherRequest(

                @NotBlank(message = "Name is required") @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters") String name,

                @NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") @Size(max = 255, message = "Email must not exceed 255 characters") String email,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password,

                @Size(max = 500, message = "Imagem must not exceed 500 characters") String imagem,

                @NotBlank(message = "CPF is required") @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits") String cpf,

                @Size(max = 150, message = "Departamento must not exceed 150 characters") String departamento) {
}
