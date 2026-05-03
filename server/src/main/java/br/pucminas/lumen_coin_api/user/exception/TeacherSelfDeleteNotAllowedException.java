package br.pucminas.lumen_coin_api.user.exception;

public class TeacherSelfDeleteNotAllowedException extends RuntimeException {
    public TeacherSelfDeleteNotAllowedException() {
        super("Teachers cannot delete their own account. Please contact your institution to request account deletion.");
    }
}
