package br.pucminas.lumen_coin_api.user.controller;

import br.pucminas.lumen_coin_api.security.UserPrincipal;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeInitialPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.ChangeTeacherPasswordRequest;
import br.pucminas.lumen_coin_api.user.dto.request.RegisterTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.request.UpdateTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;
import br.pucminas.lumen_coin_api.user.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<TeacherResponse> register(
            @Valid @RequestBody RegisterTeacherRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.register(request, principal.getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> findAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTITUTION','TEACHER')")
    public ResponseEntity<TeacherResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeTeacherPasswordRequest request) {
        teacherService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/initial-password")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> changeInitialPassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeInitialPasswordRequest request) {
        teacherService.changeInitialPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTITUTION','TEACHER')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        teacherService.delete(id);
        return ResponseEntity.ok().build();
    }
}
