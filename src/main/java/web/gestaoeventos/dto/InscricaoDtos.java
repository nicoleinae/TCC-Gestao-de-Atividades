package web.gestaoeventos.dto;

import java.time.LocalDateTime;

public final class InscricaoDtos {

    private InscricaoDtos() {
    }

    public record InscricaoResponse(
            Long id,
            Long usuarioId,
            String usuarioNome,
            Long atividadeId,
            String atividadeNome,
            LocalDateTime dataInscricao) {
    }
}
