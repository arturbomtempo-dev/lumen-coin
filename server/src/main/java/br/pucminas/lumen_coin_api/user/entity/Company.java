package br.pucminas.lumen_coin_api.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import br.pucminas.lumen_coin_api.user.enums.UserRole;

@Entity
@Table(name = "tb_companies", indexes = @Index(name = "idx_companies_cnpj", columnList = "cnpj"))
@DiscriminatorValue("COMPANY")
@Getter
@Setter
@NoArgsConstructor
public class Company extends User {

    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    private String cnpj;

    @Override
    public UserRole getRole() {
        return UserRole.COMPANY;
    }
}
