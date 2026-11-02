package com.restaurante.mapper;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.persistence.entity.PlatoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlatoMapperTest {

    private final PlatoMapperIn platoMapperIn = Mappers.getMapper(PlatoMapperIn.class);
    private final PlatoMapperOut platoMapperOut = Mappers.getMapper(PlatoMapperOut.class);
    private final PlatoEntityMapper platoEntityMapper = Mappers.getMapper(PlatoEntityMapper.class);

    private final MesaMapperIn mesaMapperIn = Mappers.getMapper(MesaMapperIn.class);
    private final MesaMapperOut mesaMapperOut = Mappers.getMapper(MesaMapperOut.class);
    private final MesaEntityMapper mesaEntityMapper = Mappers.getMapper(MesaEntityMapper.class);

    private final PedidoMapperIn pedidoMapperIn = Mappers.getMapper(PedidoMapperIn.class);
    private final PedidoMapperOut pedidoMapperOut = Mappers.getMapper(PedidoMapperOut.class);
    private final PedidoEntityMapper pedidoEntityMapper = Mappers.getMapper(PedidoEntityMapper.class);

    @Test
    @DisplayName("✅ Doble mapeo de Plato: RequestDTO -> Domain -> Entity -> Domain -> ResponseDTO")
    void dobleMapeoPlato_debeConservarDatosEIgnorarIdEnCreacion() {
        // Arrange
        PlatoRequestDTO request = new PlatoRequestDTO(
                "Fettuccine Alfredo",
                26000.0,
                "PLATO_FUERTE",
                "Pasta fresca con mantequilla y parmesano",
                true
        );

        // Act
        Plato domain = platoMapperIn.toDomain(request);
        PlatoEntity entity = platoEntityMapper.toEntity(domain);
        entity.setId(7L);
        Plato domainPersistido = platoEntityMapper.toDomain(entity);
        PlatoResponseDTO response = platoMapperOut.toDTO(domainPersistido);

        // Assert
        assertNull(domain.getId());
        assertEquals(7L, response.getId());
        assertEquals("Fettuccine Alfredo", response.getNombre());
        assertEquals(26000.0, response.getPrecio());
        assertEquals("PLATO_FUERTE", response.getCategoria());
        assertTrue(response.getDisponible());
    }

    @Test
    @DisplayName("✅ Doble mapeo de Mesa: RequestDTO -> Domain -> Entity -> Domain -> ResponseDTO")
    void dobleMapeoMesa_debeMapearEstadosCorrectamente() {
        // Arrange
        MesaRequestDTO request = new MesaRequestDTO();
        request.setNumero(12);
        request.setCapacidad(6);

        // Act
        Mesa domain = mesaMapperIn.toDomain(request);
        domain.setEstado(EstadoMesa.DISPONIBLE);
        domain.setCuentaAbierta(false);

        MesaEntity entity = mesaEntityMapper.toEntity(domain);
        entity.setId(3L);
        Mesa domainDesdeBd = mesaEntityMapper.toDomain(entity);
        MesaResponseDTO response = mesaMapperOut.toDTO(domainDesdeBd);

        // Assert
        assertEquals(3L, response.getId());
        assertEquals(12, response.getNumero());
        assertEquals(6, response.getCapacidad());
        assertEquals("DISPONIBLE", response.getEstado());
        assertFalse(response.getCuentaAbierta());
    }

    @Test
    @DisplayName("✅ Doble mapeo de Pedido: RequestDTO -> Domain -> Entity -> Domain -> ResponseDTO con subtotal y total")
    void dobleMapeoPedido_debeCalcularSubtotalesYTotal() {
        // Arrange
        ItemPedidoRequestDTO itemReq = new ItemPedidoRequestDTO();
        itemReq.setIdPlato(10L);
        itemReq.setCantidad(2);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setIdMesa(4L);
        request.setItems(List.of(itemReq));

        // Act
        Pedido domain = pedidoMapperIn.toDomain(request);
        domain.setEstado(EstadoPedido.RECIBIDO);
        domain.agregarItem(ItemPedido.builder()
                .idPlato(10L)
                .nombrePlato("Pizza")
                .precioCongelado(25000.0)
                .cantidad(2)
                .build());

        PedidoEntity entity = pedidoEntityMapper.toEntity(domain);
        entity.setId(100L);
        Pedido domainDesdeBd = pedidoEntityMapper.toDomain(entity);
        PedidoResponseDTO response = pedidoMapperOut.toDTO(domainDesdeBd);

        // Assert
        assertEquals(100L, response.getId());
        assertEquals(4L, response.getIdMesa());
        assertEquals("RECIBIDO", response.getEstado());
        assertEquals(1, response.getItems().size());
        assertEquals(50000.0, response.getItems().get(0).getSubtotal());
        assertEquals(50000.0, response.getTotal());
    }
}
