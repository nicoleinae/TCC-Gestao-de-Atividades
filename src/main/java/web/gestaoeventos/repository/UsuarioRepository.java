package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.TipoPapel;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByCarteirinhaHash(String carteirinhaHash);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);

    Optional<Usuario> findByCpf(String cpf);

    boolean existsByPapeisNome(TipoPapel nome);

    List<Usuario> findAllByInstituicoes_IdAndAtivoTrueOrderByNomeAsc(Long instituicaoId);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);

    Optional<Usuario> findById(Long id);
}
