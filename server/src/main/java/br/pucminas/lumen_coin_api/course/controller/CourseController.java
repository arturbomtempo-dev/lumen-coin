package br.pucminas.lumen_coin_api.course.controller;

import br.pucminas.lumen_coin_api.course.dto.request.RegisterCourseRequest;
import br.pucminas.lumen_coin_api.course.dto.request.UpdateCourseRequest;
import br.pucminas.lumen_coin_api.course.dto.response.CourseResponse;
import br.pucminas.lumen_coin_api.course.service.CourseService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<CourseResponse> register(
            @Valid @RequestBody RegisterCourseRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.register(request, principal.getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> findAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<CourseResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUTION')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
