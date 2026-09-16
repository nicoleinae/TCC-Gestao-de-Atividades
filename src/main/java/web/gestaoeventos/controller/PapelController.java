package web.gestaoeventos.controller;

import jakarta.validation.Valid;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import web.gestaoeventos.dto.PapelDtos;
import web.gestaoeventos.service.PapelService;

@RestController
@RequestMapping
public class PapelController {

    private final PapelService papelService;

    public PapelController(PapelService papelService) {
        this.papelService = papelService;
    }

    @GetMapping("/papeis")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Set<PapelDtos.PapelResponse> listar() {
        return papelService.listar();
    }

    @PutMapping("/usuarios/{usuarioId}/papel")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PapelDtos.UsuarioPapeisResponse atribuir(
            @PathVariable Long usuarioId,
            @Valid @RequestBody PapelDtos.AtribuirPapelRequest request) {
        return papelService.atribuir(usuarioId, request);
    }
}
