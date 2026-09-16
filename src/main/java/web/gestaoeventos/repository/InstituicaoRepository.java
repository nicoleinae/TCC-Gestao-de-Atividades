package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import web.gestaoeventos.entity.Instituicao;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    List<Instituicao> findAllByAtivoTrueOrderByNomeAsc();
}
