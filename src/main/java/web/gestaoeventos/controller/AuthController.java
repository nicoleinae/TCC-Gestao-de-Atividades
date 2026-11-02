package web.gestaoeventos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.CadastroDtos;
import web.gestaoeventos.dto.LoginDtos;
import web.gestaoeventos.service.CadastroService;
import web.gestaoeventos.service.LoginService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CadastroService cadastroService;
    private final LoginService loginService;

    public AuthController(CadastroService cadastroService, LoginService loginService) {
        this.cadastroService = cadastroService;
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public LoginDtos.TokenResponse login(@Valid @RequestBody LoginDtos.LoginRequest request) {
        return loginService.login(request);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrar(
            @Valid @RequestBody CadastroDtos.CadastroRequest request) {
        cadastroService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<Void> credenciaisInvalidas(AuthenticationException exception) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }
}
