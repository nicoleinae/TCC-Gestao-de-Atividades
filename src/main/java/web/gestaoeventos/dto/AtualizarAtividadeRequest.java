package web.gestaoeventos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AtualizarAtividadeRequest(
        @NotBlank @Size(max = 200) String nome,
        @Size(max = 5000) String descricao,
        @NotNull @Min(1) Integer cargaHoraria,
        @NotNull LocalDate dataInicio,
        @NotBlank @Size(max = 20) String horarioInicio) {
}
