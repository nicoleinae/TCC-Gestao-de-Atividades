package web.gestaoeventos.dto;

import java.time.LocalDateTime;

public final class AuxiliarAtividadeDtos {

    private AuxiliarAtividadeDtos() {
    }

    public record AuxiliarResponse(
            Long usuarioId,
            String nome,
            String email,
            Long atividadeId,
            boolean ativo,
            LocalDateTime vinculadoEm) {
    }
}
