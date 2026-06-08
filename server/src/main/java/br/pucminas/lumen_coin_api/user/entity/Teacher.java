package br.pucminas.lumen_coin_api.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import br.pucminas.lumen_coin_api.user.enums.UserRole;

import java.util.UUID;

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

    @Column(name = "institution_id")
    private UUID institutionId;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    @Override
    public UserRole getRole() {
        return UserRole.TEACHER;
    }
}
