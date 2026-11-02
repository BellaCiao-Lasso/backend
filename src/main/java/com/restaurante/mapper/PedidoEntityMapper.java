package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoEntityMapper {
    
    @Mapping(target = "estado", expression = "java(entity.getEstado() != null ? com.restaurante.model.domain.EstadoPedido.valueOf(entity.getEstado()) : null)")
    Pedido toDomain(PedidoEntity entity);

    @Mapping(target = "estado", expression = "java(domain.getEstado() != null ? domain.getEstado().name() : null)")
    PedidoEntity toEntity(Pedido domain);

    ItemPedido toDomain(ItemPedidoEntity entity);

    @Mapping(target = "pedido", ignore = true)
    ItemPedidoEntity toEntity(ItemPedido domain);
}
