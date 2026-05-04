package br.pucminas.lumen_coin_api.user.mapper;

import br.pucminas.lumen_coin_api.user.dto.response.CompanyResponse;
import br.pucminas.lumen_coin_api.user.dto.response.InstitutionResponse;
import br.pucminas.lumen_coin_api.user.dto.response.StudentResponse;
import br.pucminas.lumen_coin_api.user.dto.response.TeacherResponse;
import br.pucminas.lumen_coin_api.user.entity.Company;
import br.pucminas.lumen_coin_api.user.entity.Institution;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.entity.Teacher;

public interface UserMapper {
    StudentResponse toResponse(Student student);

    TeacherResponse toResponse(Teacher teacher);

    InstitutionResponse toResponse(Institution institution);

    CompanyResponse toResponse(Company company);
}
