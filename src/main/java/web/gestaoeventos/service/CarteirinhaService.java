package web.gestaoeventos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.gestaoeventos.dto.PresencaDtos.CarteirinhaResponse;
import web.gestaoeventos.entity.Usuario;
import web.gestaoeventos.repository.UsuarioRepository;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class CarteirinhaService {

    private static final int NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";

    private final UsuarioRepository usuarioRepository;
    private final SecretKeySpec encryptionKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CarteirinhaService(
            UsuarioRepository usuarioRepository,
            @Value("${app.security.carteirinha-encryption-secret}") String encryptionSecret) {
        this.usuarioRepository = usuarioRepository;
        this.encryptionKey = criarChave(encryptionSecret);
    }

    @Transactional
    public CarteirinhaResult obterOuCriar(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseForUpdate(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));

        if (usuario.getCarteirinhaCodigoCriptografado() != null) {
            return new CarteirinhaResult(
                    new CarteirinhaResponse(recuperarCodigo(usuario)),
                    false);
        }

        byte[] codigoAleatorio = new byte[32];
        secureRandom.nextBytes(codigoAleatorio);
        String codigo = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(codigoAleatorio);

        usuario.setCarteirinhaHash(hash(codigo));
        usuario.setCarteirinhaCodigoCriptografado(criptografar(codigo));
        usuarioRepository.save(usuario);

        return new CarteirinhaResult(new CarteirinhaResponse(codigo), true);
    }

    @Transactional(readOnly = true)
    public CarteirinhaResponse buscar(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"));

        if (usuario.getCarteirinhaCodigoCriptografado() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Código da carteirinha não disponível; gere uma carteirinha para este usuário");
        }

        return new CarteirinhaResponse(recuperarCodigo(usuario));
    }

    private String recuperarCodigo(Usuario usuario) {
        String codigo = descriptografar(usuario.getCarteirinhaCodigoCriptografado());
        if (!hash(codigo).equals(usuario.getCarteirinhaHash())) {
            throw new IllegalStateException("Código criptografado da carteirinha não corresponde ao hash");
        }
        return codigo;
    }

    private String criptografar(String codigo) {
        byte[] nonce = new byte[NONCE_LENGTH];
        secureRandom.nextBytes(nonce);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    encryptionKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
            byte[] ciphertext = cipher.doFinal(codigo.getBytes(StandardCharsets.US_ASCII));
            byte[] payload = new byte[nonce.length + ciphertext.length];
            System.arraycopy(nonce, 0, payload, 0, nonce.length);
            System.arraycopy(ciphertext, 0, payload, nonce.length, ciphertext.length);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Não foi possível criptografar a carteirinha", exception);
        }
    }

    private String descriptografar(String codigoCriptografado) {
        try {
            byte[] payload = Base64.getUrlDecoder().decode(codigoCriptografado);
            if (payload.length <= NONCE_LENGTH) {
                throw new IllegalStateException("Código criptografado da carteirinha inválido");
            }

            byte[] nonce = new byte[NONCE_LENGTH];
            byte[] ciphertext = new byte[payload.length - NONCE_LENGTH];
            System.arraycopy(payload, 0, nonce, 0, nonce.length);
            System.arraycopy(payload, nonce.length, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    encryptionKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.US_ASCII);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException("Não foi possível descriptografar a carteirinha", exception);
        }
    }

    private static SecretKeySpec criarChave(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("O segredo de criptografia da carteirinha é obrigatório");
        }
        try {
            byte[] key = MessageDigest.getInstance("SHA-256")
                    .digest(("gestao-eventos-carteirinha:" + secret)
                            .getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível", exception);
        }
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

    public record CarteirinhaResult(CarteirinhaResponse response, boolean criada) {
    }
}
