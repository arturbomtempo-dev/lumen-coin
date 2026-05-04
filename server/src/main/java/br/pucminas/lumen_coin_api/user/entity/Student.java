package br.pucminas.lumen_coin_api.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import br.pucminas.lumen_coin_api.user.enums.UserRole;

import java.util.UUID;

@Entity
@Table(name = "tb_students", indexes = @Index(name = "idx_students_cpf", columnList = "cpf"))
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {

    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "rg", nullable = false, length = 10)
    private String rg;

    @Column(name = "zip_code", length = 8)
    private String zipCode;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "balance", nullable = false)
    private int balance = 0;

    @Column(name = "institution_id")
    private UUID institutionId;

    @Column(name = "course_id")
    private UUID courseId;

    @Override
    public UserRole getRole() {
        return UserRole.STUDENT;
    }
}
