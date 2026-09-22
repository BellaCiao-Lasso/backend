package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.response.MesaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MesaMapperOut {
    @Mapping(target = "estado", expression = "java(mesa.getEstado() != null ? mesa.getEstado().name() : null)")
    MesaResponseDTO toDTO(Mesa mesa);
}
