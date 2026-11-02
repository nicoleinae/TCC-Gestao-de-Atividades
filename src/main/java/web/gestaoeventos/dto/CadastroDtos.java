package web.gestaoeventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import web.gestaoeventos.entity.enums.Sexo;

import java.time.LocalDate;

public final class CadastroDtos {

    private CadastroDtos() {
    }

    public record CadastroRequest(
            @NotBlank @Size(max = 150) String nome,

            @NotBlank
            @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
            String cpf,

            @NotNull @Past LocalDate dataNascimento,

            Sexo sexo,

            @NotBlank @Email @Size(max = 150) String email,

            @NotBlank @Size(min = 8, max = 72) String senha,

            @Size(max = 20) String telefone,

            @Size(max = 255) String logradouro,

            @Size(max = 20) String numero,

            @Size(max = 100) String complemento,

            @Size(max = 100) String bairro,

            @Size(max = 100) String cidade,

            @Size(min = 2, max = 2) String estado,

            @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos")
            String cep) {
    }
}
