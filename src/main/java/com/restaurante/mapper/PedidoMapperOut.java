package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.Mapping;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoMapperOut {
    @Mapping(target = "estado", expression = "java(pedido.getEstado() != null ? pedido.getEstado().name() : null)")
    PedidoResponseDTO toDTO(Pedido pedido);
}
