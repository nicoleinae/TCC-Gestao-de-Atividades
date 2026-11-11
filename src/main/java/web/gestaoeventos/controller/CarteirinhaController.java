package web.gestaoeventos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.PresencaDtos.CarteirinhaResponse;
import web.gestaoeventos.service.CarteirinhaService;
import web.gestaoeventos.service.CarteirinhaService.CarteirinhaResult;

@RestController
@RequestMapping("/usuarios/me/carteirinha")
public class CarteirinhaController {

    private final CarteirinhaService carteirinhaService;

    public CarteirinhaController(CarteirinhaService carteirinhaService) {
        this.carteirinhaService = carteirinhaService;
    }

    @PostMapping
    public ResponseEntity<CarteirinhaResponse> obterOuCriar(Authentication authentication) {
        CarteirinhaResult result = carteirinhaService.obterOuCriar(authentication.getName());
        HttpStatus status = result.criada() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.response());
    }

    @GetMapping
    public CarteirinhaResponse buscar(Authentication authentication) {
        return carteirinhaService.buscar(authentication.getName());
    }
}
