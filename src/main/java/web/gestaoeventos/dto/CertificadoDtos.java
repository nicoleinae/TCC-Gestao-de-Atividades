package web.gestaoeventos.dto;

import java.time.LocalDateTime;

public final class CertificadoDtos {

    private CertificadoDtos() {
    }

    public record CertificadoResponse(
            Long id,
            Long inscricaoId,
            String caminhoArquivo,
            LocalDateTime dataEmissao) {
    }
}
