package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoMesa;
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
        when(mesaService.obtenerTodas()).thenReturn(List.of(mesaResponse));
        when(platoService.existeYEstaDisponible(10L)).thenReturn(true);
        when(platoService.obtenerPorId(10L)).thenReturn(platoResponse);
        when(mapperIn.toDomain(any())).thenReturn(pedidoDomain);
        when(entityMapper.toEntity(any(Pedido.class))).thenReturn(pedidoEntity);
        when(pedidoRepository.save(any())).thenReturn(pedidoEntity);
        when(entityMapper.toDomain(any(PedidoEntity.class))).thenReturn(pedidoDomain);
        when(mapperOut.toDTO(any())).thenReturn(responseDto);

        PedidoResponseDTO resultado = pedidoService.crearPedido(requestDto);

        assertNotNull(resultado);
        assertEquals("RECIBIDO", resultado.getEstado());
    }

    @Test
    @DisplayName("❌ Crear pedido en mesa libre - lanza EstadoInvalidoException")
    void crearPedido_mesaLibre_lanzaExcepcion() {
        mesaResponse.setEstado("DISPONIBLE");
        when(mesaService.obtenerTodas()).thenReturn(List.of(mesaResponse));

        assertThrows(EstadoInvalidoException.class, () -> pedidoService.crearPedido(requestDto));
    }
}
