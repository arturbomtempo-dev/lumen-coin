package br.pucminas.lumen_coin_api.course.service.impl;

import br.pucminas.lumen_coin_api.course.dto.request.RegisterCourseRequest;
import br.pucminas.lumen_coin_api.course.dto.request.UpdateCourseRequest;
import br.pucminas.lumen_coin_api.course.dto.response.CourseResponse;
import br.pucminas.lumen_coin_api.course.entity.Course;
import br.pucminas.lumen_coin_api.course.exception.CourseNotFoundException;
import br.pucminas.lumen_coin_api.course.mapper.CourseMapper;
import br.pucminas.lumen_coin_api.course.repository.CourseRepository;
import br.pucminas.lumen_coin_api.course.service.CourseService;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final CourseMapper mapper;

    @Override
    @Transactional
    public CourseResponse register(RegisterCourseRequest request, UUID institutionId) {
        Course course = new Course();
        course.setName(request.name());
        course.setShift(request.shift());
        course.setPeriods(request.periods());
        course.setInstitutionId(institutionId);
        return mapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findById(UUID id) {
        return courseRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    @Override
    @Transactional
    public CourseResponse update(UUID id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        if (request.name() != null)
            course.setName(request.name());
        if (request.shift() != null)
            course.setShift(request.shift());
        if (request.periods() != null)
            course.setPeriods(request.periods());

        return mapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        List<Student> students = studentRepository.findByCourseId(id);
        students.forEach(s -> s.setCourseId(null));
        studentRepository.saveAll(students);

        courseRepository.delete(course);
    }
}
