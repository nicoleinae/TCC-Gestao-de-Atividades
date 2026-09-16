package web.gestaoeventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String senha) {
    }

    public record CadastroRequest(
            @NotBlank @Size(max = 150) String nome,
            @NotBlank @Size(min = 11, max = 11) String cpf,
            @NotNull @Past LocalDate dataNascimento,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 8, max = 72) String senha) {
    }

    public record TokenResponse(String token, String tipo) {
    }
}
