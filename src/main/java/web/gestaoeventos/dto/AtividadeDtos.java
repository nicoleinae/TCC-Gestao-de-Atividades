package web.gestaoeventos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.gestaoeventos.entity.enums.StatusAtividade;

import java.time.LocalDate;

public final class AtividadeDtos {

    private AtividadeDtos() {
    }

    public record CriarAtividadeRequest(
            @NotNull Long instituicaoId,
            @NotBlank @Size(max = 200) String nome,
            @Size(max = 5000) String descricao,
            @NotNull @Min(1) Integer cargaHoraria,
            @NotNull LocalDate dataInicio,
            @NotBlank @Size(max = 20) String horarioInicio) {
    }

    public record AlterarStatusRequest(@NotNull StatusAtividade status) {
    }

    public record AtividadeResponse(
            Long id,
            Long instituicaoId,
            String nome,
            String descricao,
            Integer cargaHoraria,
            LocalDate dataInicio,
            String horarioInicio,
            String status) {
    }
}
