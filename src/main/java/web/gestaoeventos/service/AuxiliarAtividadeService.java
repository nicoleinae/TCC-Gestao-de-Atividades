package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.AuxiliarAtividadeDtos.AuxiliarResponse;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.AuxiliarAtividade;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.mapper.AuxiliarAtividadeMapper;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.AuxiliarAtividadeRepository;
import web.gestaoeventos.repository.UsuarioRepository;

import java.util.List;

@Service
public class AuxiliarAtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final AuxiliarAtividadeRepository auxiliarRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuxiliarAtividadeMapper auxiliarMapper;

    public AuxiliarAtividadeService(
            AtividadeRepository atividadeRepository,
            AuxiliarAtividadeRepository auxiliarRepository,
            UsuarioRepository usuarioRepository,
            AuxiliarAtividadeMapper auxiliarMapper) {
        this.atividadeRepository = atividadeRepository;
        this.auxiliarRepository = auxiliarRepository;
        this.usuarioRepository = usuarioRepository;
        this.auxiliarMapper = auxiliarMapper;
    }

    @Transactional
    public AuxiliarResponse vincular(
            Long atividadeId,
            String emailAuxiliar,
            String emailResponsavel) {
        Atividade atividade = buscarAtividade(atividadeId);
        Usuario responsavel = buscarUsuario(emailResponsavel);
        validarAcessoAInstituicao(responsavel, atividade);

        Usuario auxiliar = usuarioRepository.findByEmailIgnoreCase(emailAuxiliar)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário auxiliar não encontrado"));

        AuxiliarAtividade vinculo = auxiliarRepository
                .findByAtividadeIdAndAuxiliarId(atividadeId, auxiliar.getId())
                .orElseGet(AuxiliarAtividade::new);
        vinculo.setAtividade(atividade);
        vinculo.setAuxiliar(auxiliar);
        vinculo.setAtivo(true);

        return auxiliarMapper.toResponse(auxiliarRepository.save(vinculo));
    }

    @Transactional(readOnly = true)
    public List<AuxiliarResponse> listar(
            Long atividadeId,
            String emailUsuario) {
        Atividade atividade = buscarAtividade(atividadeId);

        if (emailUsuario != null) {
            Usuario usuario = buscarUsuario(emailUsuario);
            boolean colaboradorVinculado = usuario.getInstituicoes().stream()
                    .anyMatch(instituicao -> instituicao.getId().equals(
                            atividade.getInstituicao().getId()));
            boolean auxiliarVinculado = auxiliarRepository
                    .findByAtividadeIdAndAuxiliarId(atividadeId, usuario.getId())
                    .filter(AuxiliarAtividade::isAtivo)
                    .isPresent();

            if (!colaboradorVinculado && !auxiliarVinculado) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não está vinculado à atividade");
            }
        }

        return auxiliarRepository.findAllByAtividadeIdAndAtivoTrue(atividadeId)
                .stream()
        .map(auxiliarMapper::toResponse)
                .toList();
    }

    @Transactional
    public void desvincular(Long atividadeId, Long usuarioId, String emailResponsavel) {
        Atividade atividade = buscarAtividade(atividadeId);
        Usuario responsavel = buscarUsuario(emailResponsavel);
        validarAcessoAInstituicao(responsavel, atividade);

        AuxiliarAtividade vinculo = auxiliarRepository
                .findByAtividadeIdAndAuxiliarId(atividadeId, usuarioId)
                .filter(AuxiliarAtividade::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vínculo de auxiliar não encontrado"));
        vinculo.setAtivo(false);
        auxiliarRepository.save(vinculo);
    }

    private Atividade buscarAtividade(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Atividade não encontrada"));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));
    }

    private void validarAcessoAInstituicao(Usuario usuario, Atividade atividade) {
        boolean colaboradorVinculado = usuario.getInstituicoes()
                .stream()
                .anyMatch(instituicao -> instituicao.getId().equals(
                        atividade.getInstituicao().getId()));
        if (!colaboradorVinculado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário não está vinculado à instituição da atividade");
        }
    }

}
