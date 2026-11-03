package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoeventos.entity.Inscricao;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    Optional<Inscricao> findByUsuarioIdAndAtividadeId(Long usuarioId, Long atividadeId);

    List<Inscricao> findAllByAtividadeIdOrderByDataInscricaoAsc(Long atividadeId);
}
