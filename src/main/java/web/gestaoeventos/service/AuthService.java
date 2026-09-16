package web.gestaoeventos.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import web.gestaoeventos.dto.AuthDtos;
import web.gestaoeventos.entity.Login;
import web.gestaoeventos.entity.Papel;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.entity.enums.TipoPapel;
import web.gestaoeventos.repository.LoginRepository;
import web.gestaoeventos.repository.PapelRepository;
import web.gestaoeventos.repository.UsuarioRepository;
import web.gestaoeventos.security.TokenService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final LoginRepository loginRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            LoginRepository loginRepository,
            PapelRepository papelRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.loginRepository = loginRepository;
        this.papelRepository = papelRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().trim().toLowerCase(Locale.ROOT),
                        request.senha()));

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas"));

        return new AuthDtos.TokenResponse(tokenService.gerar(usuario), "Bearer");
    }

    @Transactional
    public void cadastrar(AuthDtos.CadastroRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmailIgnoreCase(email)
                || loginRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        if (usuarioRepository.existsByCpf(request.cpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        Papel papelUsuario = papelRepository.findByNome(TipoPapel.USUARIO)
                .orElseGet(() -> papelRepository.save(criarPapelUsuario()));

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setCpf(request.cpf());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setEmail(email);
        usuario.getPapeis().add(papelUsuario);
        usuario = usuarioRepository.save(usuario);

        Login login = new Login();
        login.setUsuario(usuario);
        login.setEmail(email);
        login.setSenha(passwordEncoder.encode(request.senha()));
        loginRepository.save(login);
    }

    private Papel criarPapelUsuario() {
        Papel papel = new Papel();
        papel.setNome(TipoPapel.USUARIO);
        return papel;
    }
}
