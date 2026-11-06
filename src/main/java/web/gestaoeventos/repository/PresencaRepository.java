package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoeventos.entity.Presenca;

import java.util.List;

public interface PresencaRepository extends JpaRepository<Presenca, Long> {

    boolean existsByInscricaoId(Long inscricaoId);

    @EntityGraph(attributePaths = "inscricao")
    List<Presenca> findAllByInscricaoAtividadeId(Long atividadeId);
}
