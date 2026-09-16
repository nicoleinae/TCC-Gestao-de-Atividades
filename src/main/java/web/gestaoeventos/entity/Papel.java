package web.gestaoeventos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import web.gestaoeventos.entity.enums.TipoPapel;

@Entity
@Table(name = "Papel")
@Getter
@Setter
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nome", nullable = false, unique = true, length = 50)
    private TipoPapel nome;

    @ManyToMany(mappedBy = "papeis", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();
}
