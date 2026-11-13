package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.AuxiliarAtividadeDtos;
import web.gestaoeventos.entity.AuxiliarAtividade;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuxiliarAtividadeMapper {

    @Mapping(target = "usuarioId", source = "auxiliar.id")
    @Mapping(target = "nome", source = "auxiliar.nome")
    @Mapping(target = "email", source = "auxiliar.email")
    @Mapping(target = "atividadeId", source = "atividade.id")
    @Mapping(target = "vinculadoEm", ignore = true)
    AuxiliarAtividadeDtos.AuxiliarResponse toResponse(AuxiliarAtividade vinculo);
}
