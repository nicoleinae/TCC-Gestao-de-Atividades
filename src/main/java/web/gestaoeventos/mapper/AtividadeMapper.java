package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.AtividadeDtos;
import web.gestaoeventos.dto.AtualizarAtividadeRequest;
import web.gestaoeventos.entity.Atividade;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AtividadeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "status", constant = "RASCUNHO")
    @Mapping(target = "ativo", constant = "true")
    Atividade toEntity(AtividadeDtos.CriarAtividadeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    void update(AtualizarAtividadeRequest request, @MappingTarget Atividade atividade);

    @Mapping(target = "instituicaoId", source = "instituicao.id")
    AtividadeDtos.AtividadeResponse toResponse(Atividade atividade);
}
