package br.pucminas.lumen_coin_api.auth.service;

import br.pucminas.lumen_coin_api.auth.dto.request.ForgotPasswordRequest;
import br.pucminas.lumen_coin_api.auth.dto.request.LoginRequest;
import br.pucminas.lumen_coin_api.auth.dto.request.ResetPasswordRequest;
import br.pucminas.lumen_coin_api.auth.dto.response.AuthResponse;
import br.pucminas.lumen_coin_api.auth.dto.response.MeResponse;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpServletResponse response);

    AuthResponse logout(HttpServletResponse response);

    MeResponse me(UserPrincipal principal);

    AuthResponse forgotPassword(ForgotPasswordRequest request);

    AuthResponse resetPassword(ResetPasswordRequest request);
}
