package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Inscricao", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"usuario_id", "atividade_id"}))
@Getter @Setter
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @CreationTimestamp
    @Column(name = "data_inscricao", updatable = false)
    private LocalDateTime dataInscricao;

    @Column(name = "presenca_confirmada")
    private boolean presencaConfirmada = false;

}

