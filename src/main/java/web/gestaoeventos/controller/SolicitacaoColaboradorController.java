package web.gestaoeventos.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import web.gestaoeventos.dto.SolicitacaoColaboradorDtos;
import web.gestaoeventos.service.SolicitacaoColaboradorService;

@RestController
@RequestMapping("/solicitacoes-colaborador")
public class SolicitacaoColaboradorController {

    private final SolicitacaoColaboradorService solicitacaoService;

    public SolicitacaoColaboradorController(
            SolicitacaoColaboradorService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoColaboradorDtos.SolicitacaoResponse criar(
            Authentication authentication,
            @Valid @RequestBody SolicitacaoColaboradorDtos.CriarSolicitacaoRequest request) {
        return solicitacaoService.criar(authentication.getName(), request);
    }

    @GetMapping
    public List<SolicitacaoColaboradorDtos.SolicitacaoResponse> listarPendentes() {
        return solicitacaoService.listarPendentes();
    }

    @PutMapping("/{id}/aprovar")
    public SolicitacaoColaboradorDtos.SolicitacaoResponse aprovar(@PathVariable Long id) {
        return solicitacaoService.aprovar(id);
    }

    @PutMapping("/{id}/rejeitar")
    public SolicitacaoColaboradorDtos.SolicitacaoResponse rejeitar(@PathVariable Long id) {
        return solicitacaoService.rejeitar(id);
    }
}
