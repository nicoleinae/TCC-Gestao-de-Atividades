package web.gestaoeventos.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.InscricaoDtos.InscricaoResponse;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.Inscricao;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.StatusAtividade;
import web.gestaoeventos.mapper.InscricaoMapper;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.InscricaoRepository;
import web.gestaoeventos.repository.UsuarioRepository;

import java.util.List;

@Service
public class InscricaoService {

    private final AtividadeRepository atividadeRepository;
    private final InscricaoRepository inscricaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscricaoMapper inscricaoMapper;

    public InscricaoService(
            AtividadeRepository atividadeRepository,
            InscricaoRepository inscricaoRepository,
            UsuarioRepository usuarioRepository,
            InscricaoMapper inscricaoMapper) {
        this.atividadeRepository = atividadeRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscricaoMapper = inscricaoMapper;
    }

    @Transactional
    public InscricaoResponse inscrever(String email, Long atividadeId) {
        Usuario usuario = buscarUsuario(email);
        Atividade atividade = buscarAtividade(atividadeId);

        if (atividade.getStatus() != StatusAtividade.PUBLICADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inscrições estão disponíveis somente para atividades publicadas");
        }

        if (inscricaoRepository.findByUsuarioIdAndAtividadeId(usuario.getId(), atividadeId)
                .isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Usuário já está inscrito nesta atividade");
        }

        Inscricao inscricao = inscricaoMapper.toEntity(usuario, atividade);

        try {
            return inscricaoMapper.toResponse(inscricaoRepository.saveAndFlush(inscricao));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Usuário já está inscrito nesta atividade",
                    exception);
        }
    }

    @Transactional
    public void desinscrever(String email, Long atividadeId) {
        Usuario usuario = buscarUsuario(email);
        buscarAtividade(atividadeId);

        Inscricao inscricao = inscricaoRepository
                .findByUsuarioIdAndAtividadeId(usuario.getId(), atividadeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inscrição não encontrada"));

        inscricaoRepository.delete(inscricao);
    }

    @Transactional(readOnly = true)
    public List<InscricaoResponse> listarInscritos(String email, Long atividadeId) {
        Usuario usuario = buscarUsuario(email);
        Atividade atividade = buscarAtividade(atividadeId);

        boolean colaboradorVinculado = usuario.getInstituicoes().stream()
                .anyMatch(instituicao -> instituicao.getId()
                        .equals(atividade.getInstituicao().getId()));
        if (!colaboradorVinculado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário não está vinculado à instituição da atividade");
        }

        return inscricaoRepository.findAllByAtividadeIdOrderByDataInscricaoAsc(atividadeId)
                .stream()
                .map(inscricaoMapper::toResponse)
                .toList();
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));
    }

    private Atividade buscarAtividade(Long id) {
        return atividadeRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Atividade não encontrada"));
    }
}
