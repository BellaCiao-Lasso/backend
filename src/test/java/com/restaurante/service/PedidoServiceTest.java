package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PlatoService platoService;
    
    @Mock
    private MesaService mesaService;

    @Mock
    private PedidoMapperIn mapperIn;

    @Mock
    private PedidoMapperOut mapperOut;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private PedidoRequestDTO requestDto;
    private MesaResponseDTO mesaResponse;
    private PlatoResponseDTO platoResponse;
    private Pedido pedidoEntidad;
    private PedidoResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setIdPlato(10L);
        item.setCantidad(2);

        requestDto = new PedidoRequestDTO();
        requestDto.setIdMesa(1L);
        requestDto.setItems(List.of(item));

        mesaResponse = new MesaResponseDTO();
        mesaResponse.setId(1L);
        mesaResponse.setEstado("OCUPADA");

        platoResponse = new PlatoResponseDTO();
        platoResponse.setNombre("Hamburguesa");
        platoResponse.setPrecio(20000.0);

        pedidoEntidad = new Pedido();
        pedidoEntidad.setIdMesa(1L);

        responseDto = new PedidoResponseDTO();
        responseDto.setId(100L);
        responseDto.setEstado("RECIBIDO");
        responseDto.setTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("✅ Crear pedido válido")
    void crearPedido_valido_retornaDTO() {
        when(mesaService.obtenerTodas()).thenReturn(List.of(mesaResponse));
        when(mapperIn.toDomain(requestDto)).thenReturn(pedidoEntidad);
        when(platoService.existeYEstaDisponible(10L)).thenReturn(true);
        when(platoService.obtenerPorId(10L)).thenReturn(platoResponse);
        when(mapperOut.toDTO(any(Pedido.class))).thenReturn(responseDto);

        PedidoResponseDTO resultado = pedidoService.crearPedido(requestDto);

        assertNotNull(resultado);
        assertEquals("RECIBIDO", resultado.getEstado());
        verify(platoService, times(1)).existeYEstaDisponible(10L);
    }

    @Test
    @DisplayName("❌ Crear pedido en mesa libre lanza excepción")
    void crearPedido_mesaLibre_lanzaExcepcion() {
        mesaResponse.setEstado("DISPONIBLE");
        when(mesaService.obtenerTodas()).thenReturn(List.of(mesaResponse));

        assertThrows(
                EstadoInvalidoException.class,
                () -> pedidoService.crearPedido(requestDto)
        );
    }

    @Test
    @DisplayName("❌ Crear pedido con plato no disponible lanza excepción")
    void crearPedido_platoNoDisponible_lanzaExcepcion() {
        when(mesaService.obtenerTodas()).thenReturn(List.of(mesaResponse));
        when(mapperIn.toDomain(requestDto)).thenReturn(pedidoEntidad);
        when(platoService.existeYEstaDisponible(10L)).thenReturn(false);

        assertThrows(
                EstadoInvalidoException.class,
                () -> pedidoService.crearPedido(requestDto)
        );
    }
}
