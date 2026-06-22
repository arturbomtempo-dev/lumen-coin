package br.pucminas.lumen_coin_api.benefit.mapper;

import br.pucminas.lumen_coin_api.benefit.dto.response.BenefitResponse;
import br.pucminas.lumen_coin_api.benefit.entity.Benefit;
import org.springframework.stereotype.Component;

@Component
public class BenefitMapper {

    public BenefitResponse toResponse(Benefit benefit) {
        if (benefit == null) return null;
        return new BenefitResponse(
                benefit.getId(),
                benefit.getCompanyId(),
                benefit.getInstitutionId(),
                benefit.getName(),
                benefit.getDescription(),
                benefit.getImageUrl(),
                benefit.getCost(),
                benefit.getCreatedAt(),
                benefit.getUpdatedAt()
        );
    }
}
