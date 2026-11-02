package web.gestaoeventos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.AtividadeDtos;
import web.gestaoeventos.dto.AtualizarAtividadeRequest;
import web.gestaoeventos.service.AtividadeService;

import java.util.List;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @GetMapping
    public List<AtividadeDtos.AtividadeResponse> listar(Authentication authentication) {
        return atividadeService.listar(authentication.getName());
    }

    @GetMapping("/{id}")
    public AtividadeDtos.AtividadeResponse buscarPorId(
            Authentication authentication,
            @PathVariable Long id) {
        return atividadeService.buscarPorId(authentication.getName(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadeDtos.AtividadeResponse criar(
            Authentication authentication,
            @Valid @RequestBody AtividadeDtos.CriarAtividadeRequest request) {
        return atividadeService.criar(authentication, request);
    }

    @PutMapping("/{id}")
    public AtividadeDtos.AtividadeResponse atualizar(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AtualizarAtividadeRequest request) {
        return atividadeService.atualizar(authentication.getName(), id, request);
    }

    @PatchMapping("/{id}/status")
    public AtividadeDtos.AtividadeResponse alterarStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AtividadeDtos.AlterarStatusRequest request) {
        return atividadeService.alterarStatus(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(Authentication authentication, @PathVariable Long id) {
        atividadeService.desativar(authentication.getName(), id);
    }
}
