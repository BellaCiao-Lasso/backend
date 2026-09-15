package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoMapperIn {
    Pedido toDomain(PedidoRequestDTO dto);
}
