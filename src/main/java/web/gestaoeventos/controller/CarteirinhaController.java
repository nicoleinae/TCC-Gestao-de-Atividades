package web.gestaoeventos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.PresencaDtos.CarteirinhaResponse;
import web.gestaoeventos.service.CarteirinhaService;

@RestController
@RequestMapping("/usuarios/me/carteirinha")
public class CarteirinhaController {

    private final CarteirinhaService carteirinhaService;

    public CarteirinhaController(CarteirinhaService carteirinhaService) {
        this.carteirinhaService = carteirinhaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarteirinhaResponse rotacionar(Authentication authentication) {
        return carteirinhaService.rotacionar(authentication.getName());
    }
}
