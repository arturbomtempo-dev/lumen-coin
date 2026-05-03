package br.pucminas.lumen_coin_api.auth.service.impl;

import br.pucminas.lumen_coin_api.auth.dto.request.LoginRequest;
import br.pucminas.lumen_coin_api.auth.dto.response.AuthResponse;
import br.pucminas.lumen_coin_api.auth.service.AuthService;
import br.pucminas.lumen_coin_api.security.JwtService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        response.addCookie(jwtService.buildAuthCookie(principal));

        return new AuthResponse("Logged in successfully");
    }

    @Override
    public AuthResponse logout(HttpServletResponse response) {
        response.addCookie(jwtService.buildClearCookie());
        return new AuthResponse("Logged out successfully");
    }
}
