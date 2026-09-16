package web.gestaoeventos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import web.gestaoeventos.entity.Login;

public interface LoginRepository extends JpaRepository<Login, Long> {

    @EntityGraph(attributePaths = {"usuario", "usuario.papeis"})
    Optional<Login> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
