package web.gestaoeventos.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import web.gestaoeventos.dto.AtividadeDtos;
import web.gestaoeventos.service.AtividadeService;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadeDtos.AtividadeResponse criar(
            Authentication authentication,
            @Valid @RequestBody AtividadeDtos.CriarAtividadeRequest request) {
        return atividadeService.criar(authentication, request);
    }
}
