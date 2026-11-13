package web.gestaoeventos.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.gestaoeventos.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where lower(u.email) = lower(:email)")
    Optional<Usuario> findByEmailIgnoreCaseForUpdate(@Param("email") String email);

    Optional<Usuario> findByCarteirinhaHash(String carteirinhaHash);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);

    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findAllByInstituicoes_IdAndAtivoTrueOrderByNomeAsc(Long instituicaoId);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);

    Optional<Usuario> findById(Long id);
}
