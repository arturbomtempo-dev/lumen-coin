package br.pucminas.lumen_coin_api.user.dto.request;

import jakarta.validation.constraints.*;

public record RegisterTeacherRequest(

                @NotBlank(message = "O nome é obrigatório") @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres") String name,

                @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O e-mail informado não é válido") @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres") String email,

                @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos (apenas números, sem código do país)") String phone,

                @NotBlank(message = "O CPF é obrigatório") @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos") String cpf,

                @Size(max = 150, message = "O departamento deve ter no máximo 150 caracteres") String department) {
}
