package br.pucminas.lumen_coin_api.auth.service;

import br.pucminas.lumen_coin_api.auth.dto.request.LoginRequest;
import br.pucminas.lumen_coin_api.auth.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpServletResponse response);

    void logout(HttpServletResponse response);
}
