package web.gestaoeventos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import web.gestaoeventos.entity.Papel;
import web.gestaoeventos.entity.enums.TipoPapel;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    Optional<Papel> findByNome(TipoPapel nome);

    boolean existsByNome(TipoPapel nome);
}
