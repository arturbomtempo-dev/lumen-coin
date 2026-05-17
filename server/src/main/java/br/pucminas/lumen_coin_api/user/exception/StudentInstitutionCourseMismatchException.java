package br.pucminas.lumen_coin_api.user.exception;

import java.util.UUID;

public class StudentInstitutionCourseMismatchException extends RuntimeException {
    public StudentInstitutionCourseMismatchException(UUID institutionId, UUID courseId) {
        super("O curso selecionado não pertence à instituição informada");
    }
}
