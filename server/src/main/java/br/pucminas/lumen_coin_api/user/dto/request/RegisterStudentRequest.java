package br.pucminas.lumen_coin_api.user.dto.request;

import br.pucminas.lumen_coin_api.user.enums.Avatar;
import jakarta.validation.constraints.*;

public record RegisterStudentRequest(

                @NotBlank(message = "O nome é obrigatório") @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres") String name,

                @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O e-mail informado não é válido") @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres") String email,

                @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos (apenas números, sem código do país)") String phone,

                @NotBlank(message = "A senha é obrigatória") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,72}$", message = "A senha deve ter no mínimo 8 caracteres, com pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial") String password,

                @NotNull(message = "O avatar é obrigatório") Avatar avatar,

                @NotBlank(message = "O CPF é obrigatório") @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos") String cpf,

                @NotBlank(message = "O RG é obrigatório") @Pattern(regexp = "[A-Z]{2}\\d{8}", message = "O RG deve seguir o formato XX00000000 (2 letras + 8 dígitos)") String rg,

                @Pattern(regexp = "\\d{8}", message = "O CEP deve conter exatamente 8 dígitos") String zipCode,

                @Size(max = 300, message = "O endereço deve ter no máximo 300 caracteres") String address,

                @NotNull(message = "A instituição é obrigatória") java.util.UUID institutionId,

                @NotNull(message = "O curso é obrigatório") java.util.UUID courseId) {
}
