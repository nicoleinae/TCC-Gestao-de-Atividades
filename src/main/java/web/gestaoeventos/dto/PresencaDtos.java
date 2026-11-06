package web.gestaoeventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.List;

public final class PresencaDtos {

    private PresencaDtos() {
    }

    public record PresencaResponse(
            Long id,
            Long inscricaoId,
            Long usuarioId,
            Long atividadeId,
            LocalDateTime dataPresenca) {
    }

    public record RegistrarPresencaQrRequest(
            @NotBlank
            @Pattern(regexp = "[A-Za-z0-9_-]{43}", message = "Código de carteirinha inválido")
            String codigo) {
    }

    public record RegistrarPresencaCpfRequest(
            @NotBlank
            @Pattern(
                    regexp = "\\d{11}",
                    message = "CPF deve conter exatamente 11 dígitos")
            String cpf) {
    }

    public record CarteirinhaResponse(String codigo) {
    }

    public record PresencaRelatorioItem(
            Long inscricaoId,
            Long usuarioId,
            String usuarioNome,
            boolean presente,
            LocalDateTime dataPresenca) {
    }

    public record PresencaRelatorioResponse(
            Long atividadeId,
            String atividadeNome,
            long totalInscritos,
            long totalPresentes,
            long totalAusentes,
            List<PresencaRelatorioItem> inscritos) {
    }
}
