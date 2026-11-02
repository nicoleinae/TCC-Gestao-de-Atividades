package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoRequest;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoResponse;
import web.gestaoeventos.entity.Instituicao;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.mapper.InstituicaoMapper;
import web.gestaoeventos.repository.InstituicaoRepository;
import web.gestaoeventos.repository.UsuarioRepository;

import java.util.List;

@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoMapper instituicaoMapper;

    public InstituicaoService(
            InstituicaoRepository instituicaoRepository,
            UsuarioRepository usuarioRepository,
            InstituicaoMapper instituicaoMapper) {
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.instituicaoMapper = instituicaoMapper;
    }

    @Transactional
    public InstituicaoResponse criar(
            String emailCriador,
            InstituicaoRequest request) {
        Usuario criador = usuarioRepository.findByEmailIgnoreCase(emailCriador)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));

        Instituicao instituicao = instituicaoMapper.toEntity(request);
        instituicao.setResponsavel(criador);
        instituicao = instituicaoRepository.save(instituicao);

        criador.getInstituicoes().add(instituicao);
        usuarioRepository.save(criador);

        return instituicaoMapper.toResponse(instituicao);
    }

    @Transactional(readOnly = true)
    public List<InstituicaoResponse> listar() {
        return instituicaoRepository.findAllByAtivoTrueOrderByNomeAsc().stream()
                .map(instituicaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstituicaoResponse buscarPorId(Long id) {
        return instituicaoMapper.toResponse(buscarAtiva(id));
    }

    @Transactional
    public InstituicaoRequest atualizar(
            Long id,
            InstituicaoRequest request) {
        Instituicao instituicao = buscarAtiva(id);
        instituicaoMapper.update(request, instituicao);
        return instituicaoMapper.toRequest(instituicaoRepository.save(instituicao));
    }

    @Transactional
    public void desativar(Long id) {
        Instituicao instituicao = buscarAtiva(id);
        instituicao.setAtivo(false);
        instituicaoRepository.save(instituicao);
    }

    private Instituicao buscarAtiva(Long id) {
        Instituicao instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada"));

        if (!instituicao.isAtivo()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Instituição não encontrada");
        }

        return instituicao;
    }

}
