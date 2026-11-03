package web.gestaoeventos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.InscricaoDtos.InscricaoResponse;
import web.gestaoeventos.service.InscricaoService;

import java.util.List;

@RestController
@RequestMapping("/atividades/{atividadeId}")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @PostMapping("/inscricoes")
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponse inscrever(
            Authentication authentication,
            @PathVariable Long atividadeId) {
        return inscricaoService.inscrever(authentication.getName(), atividadeId);
    }

    @DeleteMapping("/inscricoes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desinscrever(
            Authentication authentication,
            @PathVariable Long atividadeId) {
        inscricaoService.desinscrever(authentication.getName(), atividadeId);
    }

    @GetMapping("/inscritos")
    public List<InscricaoResponse> listarInscritos(
            Authentication authentication,
            @PathVariable Long atividadeId) {
        return inscricaoService.listarInscritos(authentication.getName(), atividadeId);
    }
}
