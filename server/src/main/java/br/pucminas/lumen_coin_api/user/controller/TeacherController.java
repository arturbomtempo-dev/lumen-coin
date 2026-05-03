package br.pucminas.lumen_coin_api.user.controller;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterTeacherRequest;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;
import br.pucminas.lumen_coin_api.user.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherResponse> register(
            @Valid @RequestBody RegisterTeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.register(request));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> findAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }
}
