package web.gestaoeventos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.PresencaDtos.PresencaRelatorioResponse;
import web.gestaoeventos.dto.PresencaDtos.PresencaResponse;
import web.gestaoeventos.dto.PresencaDtos.RegistrarPresencaCpfRequest;
import web.gestaoeventos.dto.PresencaDtos.RegistrarPresencaQrRequest;
import web.gestaoeventos.service.PresencaService;

@RestController
@RequestMapping("/atividades/{atividadeId}/presencas")
public class PresencaController {

    private final PresencaService presencaService;

    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    @PostMapping("/qr")
    @ResponseStatus(HttpStatus.CREATED)
    public PresencaResponse registrarPorQr(
            Authentication authentication,
            @PathVariable Long atividadeId,
            @Valid @RequestBody RegistrarPresencaQrRequest request) {
        return presencaService.registrarPorQr(
                authentication.getName(),
                atividadeId,
                request.codigo());
    }

    @PostMapping("/cpf")
    @ResponseStatus(HttpStatus.CREATED)
    public PresencaResponse registrarPorCpf(
            Authentication authentication,
            @PathVariable Long atividadeId,
            @Valid @RequestBody RegistrarPresencaCpfRequest request) {
        return presencaService.registrarPorCpf(
                authentication.getName(),
                atividadeId,
                request.cpf());
    }

    @GetMapping
    public PresencaRelatorioResponse relatorio(
            Authentication authentication,
            @PathVariable Long atividadeId) {
        return presencaService.relatorio(authentication.getName(), atividadeId);
    }
}
