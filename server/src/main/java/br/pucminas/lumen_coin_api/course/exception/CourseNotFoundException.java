package br.pucminas.lumen_coin_api.course.exception;

import java.util.UUID;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(UUID id) {
        super("Curso não encontrado");
    }
}
