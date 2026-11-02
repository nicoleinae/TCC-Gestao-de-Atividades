package web.gestaoeventos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import web.gestaoeventos.dto.CertificadoDtos;
import web.gestaoeventos.entity.Certificado;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CertificadoMapper {

    @Mapping(target = "inscricaoId", source = "inscricao.id")
    CertificadoDtos.CertificadoResponse toResponse(Certificado certificado);
}
