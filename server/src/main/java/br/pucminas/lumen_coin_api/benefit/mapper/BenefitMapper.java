package br.pucminas.lumen_coin_api.benefit.mapper;

import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;
import br.pucminas.lumen_coin_api.benefit.entity.Benefit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BenefitMapper {
    BenefitResponse toResponse(Benefit benefit);
}