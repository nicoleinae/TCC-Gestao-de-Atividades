package web.gestaoeventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class AuxiliarAtividadeDtos {

    private AuxiliarAtividadeDtos() {
    }

    public record AdicionarAuxiliarRequest(
            @NotBlank @Email @Size(max = 150) String email) {
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
