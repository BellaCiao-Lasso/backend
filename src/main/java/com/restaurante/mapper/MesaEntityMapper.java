package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MesaEntityMapper {
    @Mapping(target = "estado", expression = "java(entity.getEstado() != null ? com.restaurante.model.domain.EstadoMesa.valueOf(entity.getEstado()) : null)")
    Mesa toDomain(MesaEntity entity);

    @Mapping(target = "estado", expression = "java(domain.getEstado() != null ? domain.getEstado().name() : null)")
    @Mapping(target = "creadoEn", ignore = true)
    MesaEntity toEntity(Mesa domain);
}
