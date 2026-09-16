package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import web.gestaoeventos.dto.AtividadeDtos;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.Instituicao;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.StatusAtividade;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.InstituicaoRepository;
import web.gestaoeventos.repository.UsuarioRepository;

@Service
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;

    public AtividadeService(
            AtividadeRepository atividadeRepository,
            InstituicaoRepository instituicaoRepository,
            UsuarioRepository usuarioRepository) {
        this.atividadeRepository = atividadeRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AtividadeDtos.AtividadeResponse criar(
            Authentication authentication,
            AtividadeDtos.CriarAtividadeRequest request) {
        Instituicao instituicao = instituicaoRepository.findById(request.instituicaoId())
                .filter(Instituicao::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada"));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));
        boolean vinculado = usuario.getInstituicoes().stream()
                .anyMatch(instituicaoAtual -> instituicaoAtual.getId().equals(instituicao.getId()));

        if (!vinculado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Colaborador não está vinculado à instituição");
        }

        Atividade atividade = new Atividade();
        atividade.setInstituicao(instituicao);
        atividade.setNome(request.nome().trim());
        atividade.setDescricao(request.descricao());
        atividade.setCargaHoraria(request.cargaHoraria());
        atividade.setDataInicio(request.dataInicio());
        atividade.setHorarioInicio(request.horarioInicio().trim());
        atividade.setStatus(StatusAtividade.RASCUNHO);

        return converter(atividadeRepository.save(atividade));
    }

    private AtividadeDtos.AtividadeResponse converter(Atividade atividade) {
        return new AtividadeDtos.AtividadeResponse(
                atividade.getId(),
                atividade.getInstituicao().getId(),
                atividade.getNome(),
                atividade.getDescricao(),
                atividade.getCargaHoraria(),
                atividade.getDataInicio(),
                atividade.getHorarioInicio(),
                atividade.getStatus().name());
    }
}
