package web.gestaoeventos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.UsuarioDtos.UsuarioMeResponse;
import web.gestaoeventos.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class
UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public UsuarioMeResponse consultarUsuarioAutenticado(Authentication authentication) {
        return usuarioService.buscarUsuarioAutenticado(authentication.getName());
    }
}
