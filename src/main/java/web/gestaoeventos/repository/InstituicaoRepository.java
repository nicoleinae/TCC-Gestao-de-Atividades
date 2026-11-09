package web.gestaoeventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoeventos.entity.Instituicao;

import java.util.List;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    List<Instituicao> findAllByAtivoTrueOrderByNomeAsc();

    //Instituicao findByIdAndAtivoTrue(Long id);
}
