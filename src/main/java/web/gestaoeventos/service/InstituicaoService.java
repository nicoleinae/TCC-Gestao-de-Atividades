package web.gestaoeventos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import web.gestaoeventos.dto.InstituicaoDtos;
import web.gestaoeventos.entity.Instituicao;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.repository.InstituicaoRepository;
import web.gestaoeventos.repository.UsuarioRepository;

@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;

    public InstituicaoService(
            InstituicaoRepository instituicaoRepository,
            UsuarioRepository usuarioRepository) {
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public InstituicaoDtos.InstituicaoResponse criar(
            InstituicaoDtos.InstituicaoRequest request) {
        Instituicao instituicao = new Instituicao();
        preencher(instituicao, request);
        return converter(instituicaoRepository.save(instituicao));
    }

    @Transactional(readOnly = true)
    public List<InstituicaoDtos.InstituicaoResponse> listar() {
        return instituicaoRepository.findAllByAtivoTrueOrderByNomeAsc().stream()
                .map(this::converter)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstituicaoDtos.InstituicaoResponse buscarPorId(Long id) {
        return converter(buscarAtiva(id));
    }

    @Transactional
    public InstituicaoDtos.InstituicaoResponse atualizar(
            Long id,
            InstituicaoDtos.InstituicaoRequest request) {
        Instituicao instituicao = buscarAtiva(id);
        preencher(instituicao, request);

        return converter(instituicaoRepository.save(instituicao));
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

    private void preencher(
            Instituicao instituicao,
            InstituicaoDtos.InstituicaoRequest request) {
        instituicao.setNome(request.nome().trim());
        instituicao.setCnpj(request.cnpj());
        instituicao.setEmail(request.email());
        instituicao.setTelefone(request.telefone());
        instituicao.setLogradouro(request.logradouro());
        instituicao.setNumero(request.numero());
        instituicao.setComplemento(request.complemento());
        instituicao.setBairro(request.bairro());
        instituicao.setCidade(request.cidade());
        instituicao.setEstado(request.estado());
        instituicao.setCep(request.cep());

        if (request.responsavelId() != null) {
            Usuario responsavel = usuarioRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Responsável não encontrado"));
            instituicao.setResponsavel(responsavel);
        }
    }

    private InstituicaoDtos.InstituicaoResponse converter(Instituicao instituicao) {
        Long responsavelId = instituicao.getResponsavel() == null
                ? null
                : instituicao.getResponsavel().getId();

        return new InstituicaoDtos.InstituicaoResponse(
                instituicao.getId(),
                instituicao.getNome(),
                instituicao.getCnpj(),
                instituicao.getEmail(),
                instituicao.getTelefone(),
                instituicao.getLogradouro(),
                instituicao.getNumero(),
                instituicao.getComplemento(),
                instituicao.getBairro(),
                instituicao.getCidade(),
                instituicao.getEstado(),
                instituicao.getCep(),
                responsavelId,
                instituicao.isAtivo());
    }
}
