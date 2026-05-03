package br.pucminas.lumen_coin_api.auth.service.impl;

import br.pucminas.lumen_coin_api.auth.dto.request.LoginRequest;
import br.pucminas.lumen_coin_api.auth.dto.response.AuthResponse;
import br.pucminas.lumen_coin_api.auth.service.AuthService;
import br.pucminas.lumen_coin_api.security.JwtService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
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
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                principal.getUserId(),
                principal.getName(),
                principal.getUsername(),
                principal.getAvatar(),
                principal.getRole());
    }
}
