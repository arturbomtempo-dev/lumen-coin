package br.pucminas.lumen_coin_api.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_teachers", indexes = @Index(name = "idx_teachers_cpf", columnList = "cpf"))
@DiscriminatorValue("TEACHER")
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends User {

    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "department", length = 150)
    private String department;

    @Column(name = "balance", nullable = false)
    private int balance = 0;
}
