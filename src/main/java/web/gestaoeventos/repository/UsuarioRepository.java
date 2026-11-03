package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.TipoPapel;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);

    boolean existsByPapeisNome(TipoPapel nome);

    Optional<Usuario> findById(Long id);
}
