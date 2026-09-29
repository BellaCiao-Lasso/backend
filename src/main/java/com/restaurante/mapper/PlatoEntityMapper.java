package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlatoEntityMapper {

    // Dominio -> Entidad JPA (para guardar en BD)
    // El id se ignora en creación – la BD lo asigna
    @Mapping(target = "creadoEn", ignore = true) // @CreationTimestamp lo llena Hibernate
    PlatoEntity toEntity(Plato plato);

    // Entidad JPA -> Dominio (al leer de BD)
    Plato toDomain(PlatoEntity entidad);
}
