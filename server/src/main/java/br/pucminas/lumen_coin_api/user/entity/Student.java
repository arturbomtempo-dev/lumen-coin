package br.pucminas.lumen_coin_api.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_students", indexes = @Index(name = "idx_students_cpf", columnList = "cpf"))
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {

    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "descricao", length = 2000)
    private String descricao;

    @Column(name = "endereco", length = 300)
    private String endereco;

    @Column(name = "saldo", nullable = false)
    private int saldo = 0;
}
