package web.gestaoeventos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import web.gestaoeventos.dto.AuxiliarAtividadeDtos;
import web.gestaoeventos.service.AuxiliarAtividadeService;

@RestController
@RequestMapping("/atividades/{atividadeId}/auxiliares")
public class AuxiliarAtividadeController {

    private final AuxiliarAtividadeService auxiliarService;

    public AuxiliarAtividadeController(AuxiliarAtividadeService auxiliarService) {
        this.auxiliarService = auxiliarService;
    }

    @GetMapping
    public List<AuxiliarAtividadeDtos.AuxiliarResponse> listar(
            Authentication authentication,
            @PathVariable Long atividadeId) {
        return auxiliarService.listar(atividadeId, authentication.getName());
    }

    @PostMapping("/{usuarioId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AuxiliarAtividadeDtos.AuxiliarResponse vincular(
            Authentication authentication,
            @PathVariable Long atividadeId,
            @PathVariable Long usuarioId) {
        return auxiliarService.vincular(
                atividadeId,
                usuarioId,
                authentication.getName());
    }

    @DeleteMapping("/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desvincular(
            Authentication authentication,
            @PathVariable Long atividadeId,
            @PathVariable Long usuarioId) {
        auxiliarService.desvincular(
                atividadeId,
                usuarioId,
                authentication.getName());
    }
}
