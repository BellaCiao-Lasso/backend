package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.persistence.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PlatoService platoService;

    @Mock
    private MesaService mesaService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoEntityMapper entityMapper;

    @Mock
    private PedidoMapperIn mapperIn;

    @Mock
    private PedidoMapperOut mapperOut;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private PedidoRequestDTO requestDto;
    private MesaResponseDTO mesaResponse;
    private PlatoResponseDTO platoResponse;
    private Pedido pedidoDomain;
    private PedidoEntity pedidoEntity;
    private PedidoResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO();
        itemDto.setIdPlato(10L);
        itemDto.setCantidad(2);

        requestDto = new PedidoRequestDTO();
        requestDto.setIdMesa(1L);
        requestDto.setItems(List.of(itemDto));

        mesaResponse = new MesaResponseDTO();
        mesaResponse.setId(1L);
        mesaResponse.setEstado("OCUPADA");

        platoResponse = new PlatoResponseDTO();
        platoResponse.setId(10L);
        platoResponse.setNombre("Hamburguesa");
        platoResponse.setPrecio(20000.0);

        pedidoDomain = new Pedido();
        pedidoDomain.setId(1L);
        pedidoDomain.setEstado(EstadoPedido.RECIBIDO);

        pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(1L);
        pedidoEntity.setEstado("RECIBIDO");

        responseDto = new PedidoResponseDTO();
        responseDto.setId(1L);
        responseDto.setEstado("RECIBIDO");
    }

    @Test
    @DisplayName("✅ Crear pedido válido - retorna DTO")
    void crearPedido_valido_retornaDTO() {
        // Arrange
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaResponse);
        when(platoService.existeYEstaDisponible(10L)).thenReturn(true);
        when(platoService.obtenerPorId(10L)).thenReturn(platoResponse);
        when(mapperIn.toDomain(any())).thenReturn(pedidoDomain);
        when(entityMapper.toEntity(any(Pedido.class))).thenReturn(pedidoEntity);
        when(pedidoRepository.save(any())).thenReturn(pedidoEntity);
        when(entityMapper.toDomain(any(PedidoEntity.class))).thenReturn(pedidoDomain);
        when(mapperOut.toDTO(any())).thenReturn(responseDto);

        // Act
        PedidoResponseDTO resultado = pedidoService.crearPedido(requestDto);

        // Assert
        assertNotNull(resultado);
        assertEquals("RECIBIDO", resultado.getEstado());
    }

    @Test
    @DisplayName("❌ Crear pedido en mesa libre - lanza EstadoInvalidoException (422)")
    void crearPedido_mesaLibre_lanzaExcepcion() {
        // Arrange
        mesaResponse.setEstado("DISPONIBLE");
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaResponse);

        // Act & Assert
        assertThrows(EstadoInvalidoException.class, () -> pedidoService.crearPedido(requestDto));
    }

    @Test
    @DisplayName("❌ Crear pedido en mesa inexistente - lanza EstadoInvalidoException (422)")
    void crearPedido_mesaInexistente_lanzaExcepcion() {
        // Arrange
        when(mesaService.obtenerPorId(1L)).thenThrow(new MesaNotFoundException("Mesa no encontrada"));

        // Act & Assert
        assertThrows(EstadoInvalidoException.class, () -> pedidoService.crearPedido(requestDto));
    }

    @Test
    @DisplayName("❌ Crear pedido con plato no disponible - lanza EstadoInvalidoException (422)")
    void crearPedido_platoNoDisponible_lanzaExcepcion() {
        // Arrange
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaResponse);
        when(mapperIn.toDomain(any())).thenReturn(pedidoDomain);
        when(platoService.existeYEstaDisponible(10L)).thenReturn(false);

        // Act & Assert
        assertThrows(EstadoInvalidoException.class, () -> pedidoService.crearPedido(requestDto));
    }

    @Test
    @DisplayName("✅ Actualizar estado válido RECIBIDO -> EN_PREPARACION")
    void actualizarEstado_transicionValida_retornaDTO() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEntity));
        when(entityMapper.toDomain(pedidoEntity)).thenReturn(pedidoDomain);
        when(entityMapper.toEntity(any(Pedido.class))).thenReturn(pedidoEntity);
        when(pedidoRepository.save(any())).thenReturn(pedidoEntity);
        responseDto.setEstado("EN_PREPARACION");
        when(mapperOut.toDTO(any())).thenReturn(responseDto);

        // Act
        PedidoResponseDTO resultado = pedidoService.actualizarEstado(1L, "EN_PREPARACION");

        // Assert
        assertEquals("EN_PREPARACION", resultado.getEstado());
        assertEquals(EstadoPedido.EN_PREPARACION, pedidoDomain.getEstado());
    }

    @Test
    @DisplayName("❌ Actualizar estado con transición bloqueada por Patrón State (RECIBIDO -> ENTREGADO) lanza EstadoInvalidoException")
    void actualizarEstado_transicionInvalida_lanzaEstadoInvalidoException() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEntity));
        when(entityMapper.toDomain(pedidoEntity)).thenReturn(pedidoDomain);

        // Act & Assert
        assertThrows(EstadoInvalidoException.class, () -> pedidoService.actualizarEstado(1L, "ENTREGADO"));
    }

    @Test
    @DisplayName("❌ Actualizar estado de pedido inexistente lanza PedidoNotFoundException (404)")
    void actualizarEstado_pedidoInexistente_lanzaPedidoNotFoundException() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.actualizarEstado(99L, "EN_PREPARACION"));
    }
}
