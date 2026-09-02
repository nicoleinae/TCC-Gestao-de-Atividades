package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Certificado")
@Getter @Setter
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscricao_id", nullable = false, unique = true)
    private Inscricao inscricao;

    @Column(name = "caminho_arquivo", nullable = false)
    private String caminhoArquivo;

    @CreationTimestamp
    @Column(name = "data_emissao", updatable = false)
    private LocalDateTime dataEmissao;

}
