package web.gestaoeventos.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import web.gestaoeventos.dto.InstituicaoDtos;
import web.gestaoeventos.service.InstituicaoService;

@RestController
@RequestMapping("/instituicoes")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @GetMapping
    public List<InstituicaoDtos.InstituicaoResponse> listar() {
        return instituicaoService.listar();
    }

    @GetMapping("/{id}")
    public InstituicaoDtos.InstituicaoResponse buscarPorId(@PathVariable Long id) {
        return instituicaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstituicaoDtos.InstituicaoResponse criar(
            @Valid @RequestBody InstituicaoDtos.InstituicaoRequest request) {
        return instituicaoService.criar(request);
    }

    @PutMapping("/{id}")
    public InstituicaoDtos.InstituicaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody InstituicaoDtos.InstituicaoRequest request) {
        return instituicaoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        instituicaoService.desativar(id);
    }
}
