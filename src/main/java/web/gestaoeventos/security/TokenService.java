package web.gestaoeventos.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import web.gestaoeventos.entity.Usuario;

@Service
public class TokenService {

    private final String secret;
    private final long expiration;

    public TokenService(
            @Value("${app.security.jwt-secret:altere-esta-chave-em-producao-gestao-eventos}") String secret,
            @Value("${app.security.jwt-expiration-ms:3600000}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String gerar(Usuario usuario) {
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long expirationTime = System.currentTimeMillis() + expiration;
        String payload = encode("{\"sub\":\"" + usuario.getEmail() + "\",\"exp\":"
                + expirationTime + "}");
        String content = header + "." + payload;

        return content + "." + assinar(content);
    }

    public String emailSeValido(String token) {
        try {
            String[] partes = token.split("\\.");

            if (partes.length != 3) {
                return null;
            }

            String content = partes[0] + "." + partes[1];
            boolean assinaturaValida = MessageDigest.isEqual(
                    assinar(content).getBytes(StandardCharsets.UTF_8),
                    partes[2].getBytes(StandardCharsets.UTF_8));

            if (!assinaturaValida) {
                return null;
            }

            String payload = new String(
                    Base64.getUrlDecoder().decode(partes[1]),
                    StandardCharsets.UTF_8);
            String email = payload.replaceAll(
                    ".*\\\"sub\\\":\\\"([^\\\"]+)\\\".*", "$1");
            long expirationTime = Long.parseLong(
                    payload.replaceAll(".*\\\"exp\\\":([0-9]+).*", "$1"));

            return expirationTime > System.currentTimeMillis()
                    && !email.equals(payload)
                    ? email
                    : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String assinar(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível assinar o token", exception);
        }
    }
}
