package br.pucminas.lumen_coin_api.user.mapper;

import br.pucminas.lumen_coin_api.user.dto.response.*;
import br.pucminas.lumen_coin_api.user.entity.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    StudentResponse toResponse(Student student);

    TeacherResponse toResponse(Teacher teacher);

    InstitutionResponse toResponse(Institution institution);

    CompanyResponse toResponse(Company company);
}
