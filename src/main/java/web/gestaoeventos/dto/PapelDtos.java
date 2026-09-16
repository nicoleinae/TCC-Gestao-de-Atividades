package web.gestaoeventos.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public final class PapelDtos {

    private PapelDtos() {
    }

    public record AtribuirPapelRequest(@NotBlank String nome) {
    }

    public record PapelResponse(Long id, String nome) {
    }

    public record UsuarioPapeisResponse(Long usuarioId, Set<String> papeis) {
    }
}
