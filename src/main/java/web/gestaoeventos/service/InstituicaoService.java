package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.InstituicaoDtos.ColaboradorResponse;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoRequest;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoResponse;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.Instituicao;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.StatusAtividade;
import web.gestaoeventos.mapper.InstituicaoMapper;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.InstituicaoRepository;
import web.gestaoeventos.repository.UsuarioRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class InstituicaoService {

    private final AtividadeRepository atividadeRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoMapper instituicaoMapper;

    public InstituicaoService(
            AtividadeRepository atividadeRepository,
            InstituicaoRepository instituicaoRepository,
            UsuarioRepository usuarioRepository,
            InstituicaoMapper instituicaoMapper) {
        this.atividadeRepository = atividadeRepository;
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

    @Transactional
    public ColaboradorResponse adicionarColaborador(
            String emailOperador,
            Long instituicaoId,
            String emailColaborador) {
        Instituicao instituicao = buscarAtiva(instituicaoId);
        Usuario operador = buscarUsuario(emailOperador);
        validarVinculo(operador, instituicao);

        Usuario colaborador = usuarioRepository.findByEmailIgnoreCase(emailColaborador)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário colaborador não encontrado"));

        boolean jaVinculado = colaborador.getInstituicoes().stream()
                .anyMatch(atual -> atual.getId().equals(instituicao.getId()));
        if (jaVinculado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Usuário já está vinculado à instituição");
        }

        colaborador.getInstituicoes().add(instituicao);
        usuarioRepository.save(colaborador);

        return new ColaboradorResponse(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getEmail(),
                instituicao.getId());
    }

    @Transactional
    public void removerColaborador(String emailOperador, Long instituicaoId, Long usuarioId) {
        Instituicao instituicao = buscarAtiva(instituicaoId);
        Usuario operador = buscarUsuario(emailOperador);
        validarVinculo(operador, instituicao);

        if (instituicao.getResponsavel() != null
                && instituicao.getResponsavel().getId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O responsável não pode ser removido da instituição");
        }

        Usuario colaborador = usuarioRepository.findByIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário colaborador não encontrado"));
        boolean removido = colaborador.getInstituicoes()
                .removeIf(atual -> atual.getId().equals(instituicaoId));
        if (!removido) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vínculo de colaborador não encontrado");
        }
        usuarioRepository.save(colaborador);
    }

    @Transactional(readOnly = true)
    public List<ColaboradorResponse> listarColaboradores(String emailOperador, Long instituicaoId) {
        Instituicao instituicao = buscarAtiva(instituicaoId);
        Usuario operador = buscarUsuario(emailOperador);
        validarVinculo(operador, instituicao);

        return usuarioRepository.findAllByInstituicoes_IdAndAtivoTrueOrderByNomeAsc(instituicaoId)
                .stream()
                .map(colaborador -> new ColaboradorResponse(
                        colaborador.getId(),
                        colaborador.getNome(),
                        colaborador.getEmail(),
                        instituicaoId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InstituicaoResponse> listarVinculadas(String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);
        return usuario.getInstituicoes().stream()
                .filter(Instituicao::isAtivo)
                .sorted(Comparator.comparing(Instituicao::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(instituicaoMapper::toResponse)
                .toList();
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
            String emailOperador,
            Long id,
            InstituicaoRequest request) {
        Instituicao instituicao = buscarAtiva(id);
        validarVinculo(buscarUsuario(emailOperador), instituicao);
        instituicaoMapper.update(request, instituicao);
        return instituicaoMapper.toRequest(instituicaoRepository.save(instituicao));
    }

    @Transactional
    public void desativar(String emailOperador, Long id) {
        Instituicao instituicao = buscarAtiva(id);
        validarVinculo(buscarUsuario(emailOperador), instituicao);

        List<Atividade> atividadesEmAndamento = atividadeRepository
                .findAllByInstituicao_IdAndStatusIn(
                        id,
                        List.of(StatusAtividade.RASCUNHO, StatusAtividade.PUBLICADA));
        atividadesEmAndamento.forEach(
                atividade -> atividade.setStatus(StatusAtividade.CANCELADA));
        atividadeRepository.saveAll(atividadesEmAndamento);

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

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado"));
    }

    private void validarVinculo(Usuario usuario, Instituicao instituicao) {
        boolean vinculado = usuario.getInstituicoes().stream()
                .anyMatch(atual -> atual.getId().equals(instituicao.getId()));
        if (!vinculado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário não está vinculado à instituição");
        }
    }

}
