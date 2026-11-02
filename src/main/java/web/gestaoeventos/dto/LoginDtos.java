package web.gestaoeventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class LoginDtos {

    private LoginDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String senha) {
    }

    public record TokenResponse(String token, String tipo) {
    }
}
