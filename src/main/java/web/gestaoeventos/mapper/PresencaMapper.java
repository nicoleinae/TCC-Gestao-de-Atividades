package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.PresencaDtos;
import web.gestaoeventos.entity.Inscricao;
import web.gestaoeventos.entity.Presenca;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PresencaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataPresenca", ignore = true)
    Presenca toEntity(Inscricao inscricao);

    @Mapping(target = "inscricaoId", source = "inscricao.id")
    @Mapping(target = "usuarioId", source = "inscricao.usuario.id")
    @Mapping(target = "atividadeId", source = "inscricao.atividade.id")
    PresencaDtos.PresencaResponse toResponse(Presenca presenca);

    @Mapping(target = "inscricaoId", source = "inscricao.id")
    @Mapping(target = "usuarioId", source = "inscricao.usuario.id")
    @Mapping(target = "usuarioNome", source = "inscricao.usuario.nome")
    @Mapping(target = "presente", expression = "java(dataPresenca != null)")
    @Mapping(target = "dataPresenca", source = "dataPresenca")
    PresencaDtos.PresencaRelatorioItem toRelatorioItem(
            Inscricao inscricao,
            LocalDateTime dataPresenca);
}
