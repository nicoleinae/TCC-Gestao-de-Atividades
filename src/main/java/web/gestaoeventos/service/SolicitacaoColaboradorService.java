package web.gestaoeventos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import web.gestaoeventos.dto.SolicitacaoColaboradorDtos;
import web.gestaoeventos.entity.Instituicao;
import web.gestaoeventos.entity.Papel;
import web.gestaoeventos.entity.SolicitacaoColaborador;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.StatusSolicitacao;
import web.gestaoeventos.entity.enums.TipoPapel;
import web.gestaoeventos.repository.InstituicaoRepository;
import web.gestaoeventos.repository.PapelRepository;
import web.gestaoeventos.repository.SolicitacaoColaboradorRepository;
import web.gestaoeventos.repository.UsuarioRepository;

@Service
public class SolicitacaoColaboradorService {

    private final InstituicaoRepository instituicaoRepository;
    private final PapelRepository papelRepository;
    private final SolicitacaoColaboradorRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoColaboradorService(
            InstituicaoRepository instituicaoRepository,
            PapelRepository papelRepository,
            SolicitacaoColaboradorRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository) {
        this.instituicaoRepository = instituicaoRepository;
        this.papelRepository = papelRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SolicitacaoColaboradorDtos.SolicitacaoResponse criar(
            String email,
            SolicitacaoColaboradorDtos.CriarSolicitacaoRequest request) {
        Usuario usuario = buscarUsuario(email);
        Instituicao instituicao = instituicaoRepository.findById(request.instituicaoId())
                .filter(Instituicao::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada"));

        boolean jaVinculado = usuario.getInstituicoes().stream()
                .anyMatch(instituicaoAtual -> instituicaoAtual.getId().equals(instituicao.getId()));
        if (jaVinculado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Usuário já está vinculado à instituição");
        }

        if (solicitacaoRepository
                .findByUsuarioIdAndInstituicaoIdAndStatus(
                        usuario.getId(),
                        instituicao.getId(),
                        StatusSolicitacao.PENDENTE)
                .isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma solicitação pendente");
        }

        SolicitacaoColaborador solicitacao = new SolicitacaoColaborador();
        solicitacao.setUsuario(usuario);
        solicitacao.setInstituicao(instituicao);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);

        return converter(solicitacaoRepository.save(solicitacao));
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoColaboradorDtos.SolicitacaoResponse> listarPendentes() {
        return solicitacaoRepository
                .findAllByStatusOrderByDataSolicitacaoAsc(StatusSolicitacao.PENDENTE)
                .stream()
                .map(this::converter)
                .toList();
    }

    @Transactional
    public SolicitacaoColaboradorDtos.SolicitacaoResponse aprovar(Long id) {
        SolicitacaoColaborador solicitacao = buscarPendente(id);
        Papel papelColaborador = papelRepository.findByNome(TipoPapel.COLABORADOR)
                .orElseGet(() -> criarPapel(TipoPapel.COLABORADOR));

        Usuario usuario = solicitacao.getUsuario();
        usuario.getPapeis().add(papelColaborador);
        usuario.getInstituicoes().add(solicitacao.getInstituicao());
        usuarioRepository.save(usuario);

        solicitacao.setStatus(StatusSolicitacao.APROVADO);
        solicitacao.setDataResposta(LocalDateTime.now());

        return converter(solicitacaoRepository.save(solicitacao));
    }

    @Transactional
    public SolicitacaoColaboradorDtos.SolicitacaoResponse rejeitar(Long id) {
        SolicitacaoColaborador solicitacao = buscarPendente(id);
        solicitacao.setStatus(StatusSolicitacao.REJEITADO);
        solicitacao.setDataResposta(LocalDateTime.now());

        return converter(solicitacaoRepository.save(solicitacao));
    }

    private SolicitacaoColaborador buscarPendente(Long id) {
        SolicitacaoColaborador solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A solicitação já foi respondida");
        }

        return solicitacao;
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));
    }

    private Papel criarPapel(TipoPapel tipoPapel) {
        Papel papel = new Papel();
        papel.setNome(tipoPapel);
        return papelRepository.save(papel);
    }

    private SolicitacaoColaboradorDtos.SolicitacaoResponse converter(
            SolicitacaoColaborador solicitacao) {
        return new SolicitacaoColaboradorDtos.SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getUsuario().getId(),
                solicitacao.getUsuario().getNome(),
                solicitacao.getInstituicao().getId(),
                solicitacao.getInstituicao().getNome(),
                solicitacao.getStatus().name(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataResposta());
    }
}
