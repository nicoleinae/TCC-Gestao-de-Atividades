package web.gestaoeventos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "AuxiliarAtividade",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_auxiliar_atividade",
                        columnNames = {"atividade_id", "auxiliar_id"}
                )
        }
)
@Getter
@Setter
public class AuxiliarAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auxiliar_id", nullable = false)
    private Usuario auxiliar;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

}
