package web.gestaoeventos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import web.gestaoeventos.dto.InstituicaoDtos.AdicionarColaboradorRequest;
import web.gestaoeventos.dto.InstituicaoDtos.ColaboradorResponse;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoRequest;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoResponse;
import web.gestaoeventos.service.InstituicaoService;

import java.util.List;

@RestController
@RequestMapping("/instituicoes")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @GetMapping
    public List<InstituicaoResponse> listar() {
        return instituicaoService.listar();
    }

    @GetMapping("/minhas")
    public List<InstituicaoResponse> listarMinhas(Authentication authentication) {
        return instituicaoService.listarVinculadas(authentication.getName());
    }

    @GetMapping("/{id}")
    public InstituicaoResponse buscarPorId(@PathVariable Long id) {
        return instituicaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstituicaoResponse criar(
            Authentication authentication,
            @Valid @RequestBody InstituicaoRequest request) {
        return instituicaoService.criar(authentication.getName(), request);
    }

    @PostMapping("/{id}/colaboradores")
    @ResponseStatus(HttpStatus.CREATED)
    public ColaboradorResponse adicionarColaborador(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AdicionarColaboradorRequest request) {
        return instituicaoService.adicionarColaborador(
                authentication.getName(),
                id,
                request.email());
    }

    @GetMapping("/{id}/colaboradores")
    public List<ColaboradorResponse> listarColaboradores(
            Authentication authentication,
            @PathVariable Long id) {
        return instituicaoService.listarColaboradores(authentication.getName(), id);
    }

    @DeleteMapping("/{id}/colaboradores/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerColaborador(
            Authentication authentication,
            @PathVariable Long id,
            @PathVariable Long usuarioId) {
        instituicaoService.removerColaborador(authentication.getName(), id, usuarioId);
    }

    @PutMapping("/{id}")
    public InstituicaoRequest atualizar(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody InstituicaoRequest request) {
        return instituicaoService.atualizar(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(Authentication authentication, @PathVariable Long id) {
        instituicaoService.desativar(authentication.getName(), id);
    }
}
