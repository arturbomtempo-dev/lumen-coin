package br.pucminas.lumen_coin_api.user.repository;

import br.pucminas.lumen_coin_api.user.entity.User;
import br.pucminas.lumen_coin_api.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findAllByRole(UserRole role);
}
