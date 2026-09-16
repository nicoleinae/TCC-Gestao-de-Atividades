package web.gestaoeventos.service;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import web.gestaoeventos.dto.PapelDtos;
import web.gestaoeventos.entity.Papel;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.TipoPapel;
import web.gestaoeventos.repository.PapelRepository;
import web.gestaoeventos.repository.UsuarioRepository;

@Service
public class PapelService {

    private final PapelRepository papelRepository;
    private final UsuarioRepository usuarioRepository;

    public PapelService(PapelRepository papelRepository, UsuarioRepository usuarioRepository) {
        this.papelRepository = papelRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Set<PapelDtos.PapelResponse> listar() {
        return papelRepository.findAll().stream()
                .map(papel -> new PapelDtos.PapelResponse(
                        papel.getId(),
                        papel.getNome().name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Transactional
    public PapelDtos.UsuarioPapeisResponse atribuir(
            Long usuarioId,
            PapelDtos.AtribuirPapelRequest request) {
        TipoPapel tipoPapel;

        try {
            tipoPapel = TipoPapel.valueOf(request.nome().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Papel inválido");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"));
        Papel papel = papelRepository.findByNome(tipoPapel)
                .orElseGet(() -> criarPapel(tipoPapel));

        boolean jaPossuiPapel = usuario.getPapeis().stream()
                .anyMatch(papelAtual -> papelAtual.getNome() == tipoPapel);

        if (tipoPapel == TipoPapel.ADMINISTRADOR
                && !jaPossuiPapel
                && usuarioRepository.existsByPapeisNome(TipoPapel.ADMINISTRADOR)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um usuário administrador");
        }

        usuario.getPapeis().add(papel);
        usuarioRepository.save(usuario);

        Set<String> papeis = usuario.getPapeis().stream()
                .map(Papel::getNome)
                .map(TipoPapel::name)
                .collect(Collectors.toUnmodifiableSet());

        return new PapelDtos.UsuarioPapeisResponse(usuario.getId(), papeis);
    }

    private Papel criarPapel(TipoPapel nome) {
        Papel papel = new Papel();
        papel.setNome(nome);
        return papelRepository.save(papel);
    }
}
