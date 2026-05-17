package br.pucminas.lumen_coin_api.user.exception;

public class TeacherSelfDeleteNotAllowedException extends RuntimeException {
    public TeacherSelfDeleteNotAllowedException() {
        super("Professores não podem excluir a própria conta. Entre em contato com sua instituição para solicitar a exclusão.");
    }
}
