package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.UsuarioDtos;
import web.gestaoeventos.entity.Usuario;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UsuarioMapper {

    @Mapping(target = "papeis", expression = "java(usuario.getPapeis().stream().map(p -> p.getNome().name()).collect(java.util.stream.Collectors.toUnmodifiableSet()))")
    @Mapping(target = "instituicaoIds", expression = "java(usuario.getInstituicoes().stream().map(i -> i.getId()).collect(java.util.stream.Collectors.toUnmodifiableSet()))")
    UsuarioDtos.UsuarioResponse toResponse(Usuario usuario);

    UsuarioDtos.UsuarioMeResponse toMeResponse(Usuario usuario);
}
