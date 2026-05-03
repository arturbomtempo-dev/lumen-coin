package br.pucminas.lumen_coin_api.user.dto.request;

import br.pucminas.lumen_coin_api.user.enums.Avatar;
import jakarta.validation.constraints.*;

public record RegisterTeacherRequest(

                @NotBlank(message = "Name is required") @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters") String name,

                @NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") @Size(max = 255, message = "Email must not exceed 255 characters") String email,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password,

                @NotNull(message = "Avatar is required") Avatar avatar,

                @NotBlank(message = "CPF is required") @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits") String cpf,

                @Size(max = 150, message = "Department must not exceed 150 characters") String department) {
}
