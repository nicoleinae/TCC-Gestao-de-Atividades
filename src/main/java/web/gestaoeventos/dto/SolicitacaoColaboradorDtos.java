package web.gestaoeventos.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class SolicitacaoColaboradorDtos {

    private SolicitacaoColaboradorDtos() {
    }

    public record CriarSolicitacaoRequest(@NotNull Long instituicaoId) {
    }

    public record SolicitacaoResponse(
            Long id,
            Long usuarioId,
            String usuarioNome,
            Long instituicaoId,
            String instituicaoNome,
            String status,
            LocalDateTime dataSolicitacao,
            LocalDateTime dataResposta) {
    }
}
