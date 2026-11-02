package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoRequest;
import web.gestaoeventos.dto.InstituicaoDtos.InstituicaoResponse;
import web.gestaoeventos.entity.Instituicao;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InstituicaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    Instituicao toEntity(InstituicaoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    void update(InstituicaoRequest request, @MappingTarget Instituicao instituicao);

    @Mapping(target = "responsavelId", source = "responsavel.id")
    InstituicaoResponse toResponse(Instituicao instituicao);

    InstituicaoRequest toRequest(Instituicao instituicao);
}
