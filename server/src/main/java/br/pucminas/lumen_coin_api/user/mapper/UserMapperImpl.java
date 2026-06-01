package br.pucminas.lumen_coin_api.user.mapper;

import br.pucminas.lumen_coin_api.user.dto.response.CompanyResponse;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;
import br.pucminas.lumen_coin_api.user.dto.response.StudentResponse;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;
import br.pucminas.lumen_coin_api.user.entity.Company;
import br.pucminas.lumen_coin_api.user.entity.Institution;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.entity.Teacher;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getAvatar(),
                student.getCpf(),
                student.getRg(),
                student.getZipCode(),
                student.getAddress(),
                student.getBalance(),
                student.getInstitutionId(),
                student.getCourseId(),
                student.getCreatedAt(),
                student.getUpdatedAt());
    }

    @Override
    public TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getName(),
                teacher.getEmail(),
                teacher.getPhone(),
                teacher.getAvatar(),
                teacher.getCpf(),
                teacher.getDepartment(),
                teacher.getBalance(),
                teacher.getInstitutionId(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt());
    }

    @Override
    public InstitutionResponse toResponse(Institution institution) {
        return new InstitutionResponse(
                institution.getId(),
                institution.getName(),
                institution.getEmail(),
                institution.getAvatar(),
                institution.getCnpj(),
                institution.getZipCode(),
                institution.getAddress(),
                institution.getCreatedAt(),
                institution.getUpdatedAt());
    }

    @Override
    public CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getEmail(),
                company.getAvatar(),
                company.getCnpj(),
                company.getCreatedAt(),
                company.getUpdatedAt());
    }
}
