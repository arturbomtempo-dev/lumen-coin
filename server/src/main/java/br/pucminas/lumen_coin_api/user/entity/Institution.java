package br.pucminas.lumen_coin_api.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_institutions", indexes = @Index(name = "idx_institutions_cnpj", columnList = "cnpj"))
@DiscriminatorValue("INSTITUTION")
@Getter
@Setter
@NoArgsConstructor
public class Institution extends User {

    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    private String cnpj;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "endereco", length = 300)
    private String endereco;
}
