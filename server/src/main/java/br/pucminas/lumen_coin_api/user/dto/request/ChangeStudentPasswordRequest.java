package br.pucminas.lumen_coin_api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeStudentPasswordRequest(

        @NotBlank(message = "A senha atual é obrigatória") String currentPassword,

        @NotBlank(message = "A nova senha é obrigatória") @Size(min = 8, max = 72, message = "A nova senha deve ter entre 8 e 72 caracteres") String newPassword,

        @NotBlank(message = "A confirmação de senha é obrigatória") String confirmNewPassword) {
}
