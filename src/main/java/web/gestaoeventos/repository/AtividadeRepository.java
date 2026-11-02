package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoeventos.entity.Atividade;

import java.util.List;
import java.util.Optional;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    List<Atividade> findAtividadesByAtivoTrue();

    Optional<Atividade> findByIdAndAtivoTrue(Long id);
}
