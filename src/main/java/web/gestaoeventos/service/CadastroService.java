package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.CadastroDtos.CadastroRequest;
import web.gestaoeventos.entity.Login;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.mapper.AuthMapper;
import web.gestaoeventos.repository.LoginRepository;
import web.gestaoeventos.repository.UsuarioRepository;

@Service
public class CadastroService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final AuthMapper authMapper;

    public CadastroService(
            LoginRepository loginRepository,
            PasswordEncoder passwordEncoder,
            UsuarioRepository usuarioRepository,
            AuthMapper authMapper) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.authMapper = authMapper;
    }

    @Transactional
    public void cadastrar(CadastroRequest request) {
        String email = authMapper.normalizarEmail(request.email());
        String cpf = authMapper.normalizarCpf(request.cpf());

        if (usuarioRepository.existsByEmailIgnoreCase(email)
                || loginRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        Usuario usuario = authMapper.toUsuario(request);
        usuario = usuarioRepository.save(usuario);

        String senhaHash = passwordEncoder.encode(request.senha());
        Login login = authMapper.toLogin(usuario, email, senhaHash);
        loginRepository.save(login);
    }
}
