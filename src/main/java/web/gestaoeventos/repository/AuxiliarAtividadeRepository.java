package web.gestaoeventos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import web.gestaoeventos.entity.AuxiliarAtividade;

public interface AuxiliarAtividadeRepository extends JpaRepository<AuxiliarAtividade, Long> {

    List<AuxiliarAtividade> findAllByAtividadeIdAndAtivoTrue(Long atividadeId);

    Optional<AuxiliarAtividade> findByAtividadeIdAndAuxiliarId(Long atividadeId, Long auxiliarId);
}
