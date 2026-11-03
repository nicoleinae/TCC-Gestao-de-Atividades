package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.LoginDtos.LoginRequest;
import web.gestaoeventos.dto.LoginDtos.TokenResponse;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.repository.UsuarioRepository;
import web.gestaoeventos.security.TokenService;

import java.util.Locale;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public LoginService(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().trim().toLowerCase(Locale.ROOT),
                        request.senha()));

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas"));

        return new TokenResponse(tokenService.gerar(usuario), "Bearer");
    }
}
