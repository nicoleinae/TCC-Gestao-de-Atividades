package web.gestaoeventos.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.PresencaDtos.PresencaRelatorioResponse;
import web.gestaoeventos.dto.PresencaDtos.PresencaResponse;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.Inscricao;
import web.gestaoeventos.entity.Presenca;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.mapper.PresencaMapper;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.AuxiliarAtividadeRepository;
import web.gestaoeventos.repository.InscricaoRepository;
import web.gestaoeventos.repository.PresencaRepository;
import web.gestaoeventos.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PresencaService {

    private final AtividadeRepository atividadeRepository;
    private final AuxiliarAtividadeRepository auxiliarRepository;
    private final InscricaoRepository inscricaoRepository;
    private final PresencaRepository presencaRepository;
    private final PresencaMapper presencaMapper;
    private final UsuarioRepository usuarioRepository;

    public PresencaService(
            AtividadeRepository atividadeRepository,
            AuxiliarAtividadeRepository auxiliarRepository,
            InscricaoRepository inscricaoRepository,
            PresencaRepository presencaRepository,
            PresencaMapper presencaMapper,
            UsuarioRepository usuarioRepository) {
        this.atividadeRepository = atividadeRepository;
        this.auxiliarRepository = auxiliarRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.presencaRepository = presencaRepository;
        this.presencaMapper = presencaMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PresencaResponse registrarPorQr(String emailOperador, Long atividadeId, String codigo) {
        Atividade atividade = buscarAtividadeAutorizada(emailOperador, atividadeId);
        Usuario participante = usuarioRepository
                .findByCarteirinhaHash(CarteirinhaService.hash(codigo))
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carteirinha não encontrada"));
        return registrar(atividade, participante);
    }

    @Transactional
    public PresencaResponse registrarPorCpf(String emailOperador, Long atividadeId, String cpf) {
        Atividade atividade = buscarAtividadeAutorizada(emailOperador, atividadeId);
        Usuario participante = usuarioRepository.findByCpf(cpf)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Participante não encontrado"));
        return registrar(atividade, participante);
    }

    @Transactional(readOnly = true)
    public PresencaRelatorioResponse relatorio(String emailOperador, Long atividadeId) {
        Atividade atividade = buscarAtividadeAutorizada(emailOperador, atividadeId);

        var inscricoes = inscricaoRepository
                .findAllByAtividadeIdOrderByDataInscricaoAsc(atividadeId);
        Map<Long, LocalDateTime> presencasPorInscricao = presencaRepository
                .findAllByInscricaoAtividadeId(atividadeId)
                .stream()
                .collect(Collectors.toMap(
                        presenca -> presenca.getInscricao().getId(),
                        Presenca::getDataPresenca));

        var inscritos = inscricoes.stream()
                .map(inscricao -> presencaMapper.toRelatorioItem(
                        inscricao,
                        presencasPorInscricao.get(inscricao.getId())))
                .toList();
        long totalPresentes = presencasPorInscricao.size();

        return new PresencaRelatorioResponse(
                atividade.getId(),
                atividade.getNome(),
                inscritos.size(),
                totalPresentes,
                inscritos.size() - totalPresentes,
                inscritos);
    }

    private PresencaResponse registrar(
            Atividade atividade,
            Usuario participante) {
        Inscricao inscricao = inscricaoRepository
                .findByUsuarioIdAndAtividadeId(participante.getId(), atividade.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Participante não está inscrito nesta atividade"));
        inscricao = inscricaoRepository.findByIdForUpdate(inscricao.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Inscrição não está mais disponível"));

        if (presencaRepository.existsByInscricaoId(inscricao.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Presença já foi registrada");
        }

        Presenca presenca = presencaMapper.toEntity(inscricao);
        try {
            return presencaMapper.toResponse(presencaRepository.saveAndFlush(presenca));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Presença já foi registrada",
                    exception);
        }
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

    private Atividade buscarAtividadeAutorizada(String emailOperador, Long atividadeId) {
        Usuario operador = buscarUsuario(emailOperador);
        Atividade atividade = buscarAtividade(atividadeId);
        validarOperador(operador, atividade);
        return atividade;
    }

    private void validarOperador(Usuario operador, Atividade atividade) {
        boolean responsavel = atividade.getInstituicao().getResponsavel() != null
                && atividade.getInstituicao().getResponsavel().getId().equals(operador.getId());
        boolean colaborador = operador.getInstituicoes().stream()
                .anyMatch(instituicao -> Objects.equals(
                        instituicao.getId(),
                        atividade.getInstituicao().getId()));
        boolean auxiliarAtivo = auxiliarRepository
                .existsByAtividadeIdAndAuxiliarIdAndAtivoTrue(
                        atividade.getId(),
                        operador.getId());

        if (!responsavel && !colaborador && !auxiliarAtivo) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente colaborador da instituição ou auxiliar ativo pode registrar presença");
        }
    }
}
