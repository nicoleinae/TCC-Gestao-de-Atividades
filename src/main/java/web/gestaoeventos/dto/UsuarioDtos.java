package web.gestaoeventos.dto;

import web.gestaoeventos.entity.enums.Sexo;

import java.time.LocalDate;
import java.util.Set;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    public record UsuarioResponse(
            Long id,
            String nome,
            String cpf,
            LocalDate dataNascimento,
            Sexo sexo,
            String email,
            String telefone,
            boolean ativo,
            Set<String> papeis,
            Set<Long> instituicaoIds) {
    }

    public record UsuarioMeResponse(Long id, String nome) {
    }
}
