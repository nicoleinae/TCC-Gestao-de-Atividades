package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.InscricaoDtos;
import web.gestaoeventos.entity.Atividade;
import web.gestaoeventos.entity.Inscricao;
import web.gestaoeventos.entity.Usuario;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InscricaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataInscricao", ignore = true)
    Inscricao toEntity(Usuario usuario, Atividade atividade);

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNome", source = "usuario.nome")
    @Mapping(target = "atividadeId", source = "atividade.id")
    @Mapping(target = "atividadeNome", source = "atividade.nome")
    InscricaoDtos.InscricaoResponse toResponse(Inscricao inscricao);
}
