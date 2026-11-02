package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.CadastroDtos;
import web.gestaoeventos.entity.Login;
import web.gestaoeventos.entity.Usuario;

import java.util.Locale;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nome", qualifiedByName = "trimOrNull")
    @Mapping(target = "cpf", source = "cpf", qualifiedByName = "normalizarCpf")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalizarEmail")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "trimOrNull")
    @Mapping(target = "logradouro", source = "logradouro", qualifiedByName = "trimOrNull")
    @Mapping(target = "numero", source = "numero", qualifiedByName = "trimOrNull")
    @Mapping(target = "complemento", source = "complemento", qualifiedByName = "trimOrNull")
    @Mapping(target = "bairro", source = "bairro", qualifiedByName = "trimOrNull")
    @Mapping(target = "cidade", source = "cidade", qualifiedByName = "trimOrNull")
    @Mapping(target = "estado", source = "estado", qualifiedByName = "normalizarEstado")
    @Mapping(target = "cep", source = "cep", qualifiedByName = "normalizarCep")
    @Mapping(target = "ativo", constant = "true")
    @Mapping(target = "papeis", ignore = true)
    @Mapping(target = "instituicoes", ignore = true)
    Usuario toUsuario(CadastroDtos.CadastroRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "email", expression = "java(email)")
    @Mapping(target = "senha", expression = "java(senhaHash)")
    Login toLogin(Usuario usuario, String email, String senhaHash);

    @Named("normalizarEmail")
    default String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Named("normalizarCpf")
    default String normalizarCpf(String cpf) {
        return trimOrNull(cpf);
    }

    @Named("normalizarCep")
    default String normalizarCep(String cep) {
        return trimOrNull(cep);
    }

    @Named("normalizarEstado")
    default String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }

        return estado.trim().toUpperCase(Locale.ROOT);
    }

    @Named("trimOrNull")
    default String trimOrNull(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}