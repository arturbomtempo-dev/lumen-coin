package br.pucminas.lumen_coin_api.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("User with ID " + id + " was not found");
    }
}
