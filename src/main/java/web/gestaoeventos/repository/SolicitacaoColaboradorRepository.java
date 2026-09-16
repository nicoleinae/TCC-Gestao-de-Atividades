package web.gestaoeventos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import web.gestaoeventos.entity.SolicitacaoColaborador;
import web.gestaoeventos.entity.enums.StatusSolicitacao;

public interface SolicitacaoColaboradorRepository
        extends JpaRepository<SolicitacaoColaborador, Long> {

    List<SolicitacaoColaborador> findAllByStatusOrderByDataSolicitacaoAsc(
            StatusSolicitacao status);

    Optional<SolicitacaoColaborador> findByUsuarioIdAndInstituicaoIdAndStatus(
            Long usuarioId,
            Long instituicaoId,
            StatusSolicitacao status);
}
