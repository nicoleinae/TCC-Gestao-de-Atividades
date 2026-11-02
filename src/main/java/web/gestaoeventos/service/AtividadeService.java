package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.AtividadeDtos;
import web.gestaoeventos.dto.AtualizarAtividadeRequest;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.Instituicao;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.mapper.AtividadeMapper;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.InstituicaoRepository;
import web.gestaoeventos.repository.UsuarioRepository;

import java.util.List;

@Service
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AtividadeMapper atividadeMapper;

    public AtividadeService(
            AtividadeRepository atividadeRepository,
            InstituicaoRepository instituicaoRepository,
            UsuarioRepository usuarioRepository,
            AtividadeMapper atividadeMapper) {
        this.atividadeRepository = atividadeRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.atividadeMapper = atividadeMapper;
    }

    @Transactional
    public AtividadeDtos.AtividadeResponse criar(
            Authentication authentication,
            AtividadeDtos.CriarAtividadeRequest request) {
        Usuario usuario = buscarUsuario(authentication.getName());
        Instituicao instituicao = buscarInstituicao(request.instituicaoId());
        validarVinculo(usuario, instituicao);

        Atividade atividade = atividadeMapper.toEntity(request);
        atividade.setInstituicao(instituicao);

        return atividadeMapper.toResponse(atividadeRepository.save(atividade));
    }

    @Transactional(readOnly = true)
    public List<AtividadeDtos.AtividadeResponse> listar(String email) {

        return atividadeRepository
                .findAtividadesByAtivoTrue()
                .stream()
                .map(atividadeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AtividadeDtos.AtividadeResponse buscarPorId(String email, Long id) {
        Atividade atividade = buscarAtiva(id);
        return atividadeMapper.toResponse(atividade);
    }

    @Transactional
    public AtividadeDtos.AtividadeResponse atualizar(
            String email,
            Long id,
            AtualizarAtividadeRequest request) {
        Usuario usuario = buscarUsuario(email);
        Atividade atividade = buscarAtiva(id);
        validarVinculo(usuario, atividade.getInstituicao());

        atividadeMapper.update(request, atividade);

        return atividadeMapper.toResponse(atividadeRepository.save(atividade));
    }

    @Transactional
    public AtividadeDtos.AtividadeResponse alterarStatus(
            String email,
            Long id,
            AtividadeDtos.AlterarStatusRequest request) {
        Usuario usuario = buscarUsuario(email);
        Atividade atividade = buscarAtiva(id);
        validarVinculo(usuario, atividade.getInstituicao());

        atividade.setStatus(request.status());
        return atividadeMapper.toResponse(atividadeRepository.save(atividade));
    }

    @Transactional
    public void desativar(String email, Long id) {
        Usuario usuario = buscarUsuario(email);
        Atividade atividade = buscarAtiva(id);
        validarVinculo(usuario, atividade.getInstituicao());

        atividade.setAtivo(false);
        atividadeRepository.save(atividade);
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));
    }

    private Instituicao buscarInstituicao(Long id) {
        return instituicaoRepository.findById(id)
                .filter(Instituicao::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada"));
    }

    private Atividade buscarAtiva(Long id) {
        return atividadeRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Atividade não encontrada"));
    }

    private void validarVinculo(Usuario usuario, Instituicao instituicao) {
        boolean vinculado = usuario.getInstituicoes().stream()
                .anyMatch(instituicaoAtual -> instituicaoAtual.getId().equals(instituicao.getId()));
        if (!vinculado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário não está vinculado à instituição");
        }
    }

}
