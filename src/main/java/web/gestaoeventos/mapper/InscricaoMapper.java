package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.InscricaoDtos;
import web.gestaoeventos.entity.Inscricao;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InscricaoMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNome", source = "usuario.nome")
    @Mapping(target = "atividadeId", source = "atividade.id")
    @Mapping(target = "atividadeNome", source = "atividade.nome")
    InscricaoDtos.InscricaoResponse toResponse(Inscricao inscricao);
}
