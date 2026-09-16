package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.gestaoeventos.entity.Atividade;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
}
