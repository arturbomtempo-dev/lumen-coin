package br.pucminas.lumen_coin_api.user.controller;

import br.pucminas.lumen_coin_api.user.dto.request.RegisterStudentRequest;
import br.pucminas.lumen_coin_api.user.dto.response.StudentResponse;
import br.pucminas.lumen_coin_api.user.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> register(
            @Valid @RequestBody RegisterStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.register(request));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> findAll() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.findById(id));
    }
}
