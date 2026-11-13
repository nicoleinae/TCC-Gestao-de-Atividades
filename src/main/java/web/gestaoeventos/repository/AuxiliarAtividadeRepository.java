package web.gestaoeventos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import web.gestaoeventos.entity.AuxiliarAtividade;

public interface AuxiliarAtividadeRepository extends JpaRepository<AuxiliarAtividade, Long> {

    List<AuxiliarAtividade> findAllByAtividadeIdAndAtivoTrue(Long atividadeId);

    Optional<AuxiliarAtividade> findByAtividadeIdAndAuxiliarId(Long atividadeId, Long auxiliarId);

    boolean existsByAtividadeIdAndAuxiliarIdAndAtivoTrue(Long atividadeId, Long auxiliarId);

    @Query("select vinculo.atividade.id from AuxiliarAtividade vinculo where vinculo.auxiliar.id = :usuarioId and vinculo.ativo = true")
    List<Long> findAtividadeIdsAtivasByUsuarioId(@Param("usuarioId") Long usuarioId);
}
