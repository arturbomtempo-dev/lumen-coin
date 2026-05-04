package br.pucminas.lumen_coin_api.auth.controller;

import br.pucminas.lumen_coin_api.auth.dto.request.LoginRequest;
import br.pucminas.lumen_coin_api.auth.dto.response.AuthResponse;
import br.pucminas.lumen_coin_api.auth.dto.response.MeResponse;
import br.pucminas.lumen_coin_api.auth.service.AuthService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(response));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(authService.me(principal));
    }
}
