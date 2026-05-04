package br.pucminas.lumen_coin_api.user.dto.request;

import br.pucminas.lumen_coin_api.user.enums.Avatar;
import jakarta.validation.constraints.*;

public record UpdateStudentRequest(

        @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters") String name,

        @Email(message = "Email must be a valid email address") @Size(max = 255, message = "Email must not exceed 255 characters") String email,

        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password,

        Avatar avatar,

        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits") String cpf,

        @Pattern(regexp = "[A-Z]{2}\\d{8}", message = "RG must follow the format XX00000000 (2 letters + 8 digits)") String rg,

        @Pattern(regexp = "\\d{8}", message = "Zip code must contain exactly 8 digits") String zipCode,

        @Size(max = 300, message = "Address must not exceed 300 characters") String address,

        java.util.UUID courseId) {
}
