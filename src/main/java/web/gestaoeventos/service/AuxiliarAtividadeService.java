package web.gestaoeventos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import web.gestaoeventos.dto.AuxiliarAtividadeDtos;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.AuxiliarAtividade;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.TipoPapel;
import web.gestaoeventos.repository.AtividadeRepository;
import web.gestaoeventos.repository.AuxiliarAtividadeRepository;
import web.gestaoeventos.repository.UsuarioRepository;

@Service
public class AuxiliarAtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final AuxiliarAtividadeRepository auxiliarRepository;
    private final UsuarioRepository usuarioRepository;

    public AuxiliarAtividadeService(
            AtividadeRepository atividadeRepository,
            AuxiliarAtividadeRepository auxiliarRepository,
            UsuarioRepository usuarioRepository) {
        this.atividadeRepository = atividadeRepository;
        this.auxiliarRepository = auxiliarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AuxiliarAtividadeDtos.AuxiliarResponse vincular(
            Long atividadeId,
            Long usuarioId,
            String emailResponsavel) {
        Atividade atividade = buscarAtividade(atividadeId);
        Usuario responsavel = buscarUsuario(emailResponsavel);
        validarAcessoAInstituicao(responsavel, atividade);

        Usuario auxiliar = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Auxiliar não encontrado"));

        boolean possuiPapelAuxiliar = auxiliar.getPapeis().stream()
                .anyMatch(papel -> papel.getNome() == TipoPapel.AUXILIAR);
        if (!possuiPapelAuxiliar) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O usuário não possui o papel AUXILIAR");
        }

        AuxiliarAtividade vinculo = auxiliarRepository
                .findByAtividadeIdAndAuxiliarId(atividadeId, usuarioId)
                .orElseGet(AuxiliarAtividade::new);
        vinculo.setAtividade(atividade);
        vinculo.setAuxiliar(auxiliar);
        vinculo.setAtivo(true);

        return converter(auxiliarRepository.save(vinculo));
    }

    @Transactional(readOnly = true)
    public List<AuxiliarAtividadeDtos.AuxiliarResponse> listar(Long atividadeId) {
        return listar(atividadeId, null);
    }

    @Transactional(readOnly = true)
    public List<AuxiliarAtividadeDtos.AuxiliarResponse> listar(
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
                .map(this::converter)
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

    private AuxiliarAtividadeDtos.AuxiliarResponse converter(AuxiliarAtividade vinculo) {
        return new AuxiliarAtividadeDtos.AuxiliarResponse(
                vinculo.getAuxiliar().getId(),
                vinculo.getAuxiliar().getNome(),
                vinculo.getAuxiliar().getEmail(),
                vinculo.getAtividade().getId(),
                vinculo.isAtivo(),
                null);
    }
}
