package web.gestaoeventos.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.PresencaDtos.CarteirinhaResponse;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.repository.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class CarteirinhaService {

    private final UsuarioRepository usuarioRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public CarteirinhaService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CarteirinhaResponse rotacionar(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));

        byte[] codigoAleatorio = new byte[32];
        secureRandom.nextBytes(codigoAleatorio);
        String codigo = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(codigoAleatorio);

        usuario.setCarteirinhaHash(hash(codigo));
        usuarioRepository.save(usuario);

        return new CarteirinhaResponse(codigo);
    }

    public static String hash(String codigo) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(codigo.getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível", exception);
        }
    }
}
