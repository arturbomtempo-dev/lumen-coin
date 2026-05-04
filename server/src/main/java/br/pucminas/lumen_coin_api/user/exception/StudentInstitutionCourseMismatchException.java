package br.pucminas.lumen_coin_api.user.exception;

import java.util.UUID;

public class StudentInstitutionCourseMismatchException extends RuntimeException {
    public StudentInstitutionCourseMismatchException(UUID institutionId, UUID courseId) {
        super("Course with ID " + courseId + " does not belong to institution with ID " + institutionId);
    }
}
