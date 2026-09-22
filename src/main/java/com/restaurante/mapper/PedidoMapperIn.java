package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.Mapping;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoMapperIn {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Pedido toDomain(PedidoRequestDTO dto);
}
