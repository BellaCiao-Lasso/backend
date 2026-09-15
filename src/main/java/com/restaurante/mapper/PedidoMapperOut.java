package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoMapperOut {
    PedidoResponseDTO toDTO(Pedido pedido);
}
