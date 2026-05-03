package br.pucminas.lumen_coin_api.user.repository;

import br.pucminas.lumen_coin_api.user.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    boolean existsByCpf(String cpf);
}
