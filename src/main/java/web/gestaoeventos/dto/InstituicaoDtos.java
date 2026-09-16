package web.gestaoeventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class InstituicaoDtos {

    private InstituicaoDtos() {
    }

    public record InstituicaoRequest(
            @NotBlank @Size(max = 150) String nome,
            @Size(max = 14) String cnpj,
            @Email @Size(max = 150) String email,
            @Size(max = 20) String telefone,
            @Size(max = 255) String logradouro,
            @Size(max = 20) String numero,
            @Size(max = 100) String complemento,
            @Size(max = 100) String bairro,
            @Size(max = 100) String cidade,
            @Size(max = 2) String estado,
            @Size(max = 8) String cep,
            Long responsavelId) {
    }

    public record InstituicaoResponse(
            Long id,
            String nome,
            String cnpj,
            String email,
            String telefone,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep,
            Long responsavelId,
            boolean ativo) {
    }
}
