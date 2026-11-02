package web.gestaoeventos.entity;

import  web.gestaoeventos.entity.enums.StatusAtividade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Atividade")
@Getter
@Setter
public class Atividade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Lob
    @Column(name = "descricao")
    private String descricao;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "horario_inicio")
    private String horarioInicio;

    @Column(name = "status")
    private StatusAtividade status = StatusAtividade.RASCUNHO;

    @Column(name = "ativo", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    private boolean ativo = true;
}
