package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.gestaoeventos.entity.Inscricao;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    Optional<Inscricao> findByUsuarioIdAndAtividadeId(Long usuarioId, Long atividadeId);

    @Query("select inscricao.atividade.id from Inscricao inscricao where inscricao.usuario.id = :usuarioId")
    List<Long> findAtividadeIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select inscricao from Inscricao inscricao where inscricao.id = :id")
    Optional<Inscricao> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = "usuario")
    List<Inscricao> findAllByAtividadeIdOrderByDataInscricaoAsc(Long atividadeId);
}
