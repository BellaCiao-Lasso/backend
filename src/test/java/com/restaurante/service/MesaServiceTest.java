package com.restaurante.service;

import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.mapper.MesaMapperIn;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

    @Mock
    private MesaMapperIn mapperIn;

    @Mock
    private MesaMapperOut mapperOut;

    @InjectMocks
    private MesaServiceImpl mesaService;

    private MesaRequestDTO requestDto;
    private Mesa mesaEntidad;
    private MesaResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new MesaRequestDTO();
        requestDto.setNumero(5);
        requestDto.setCapacidad(4);

        mesaEntidad = new Mesa(null, 5, 4, null, null);
        
        responseDto = new MesaResponseDTO();
        responseDto.setId(1L);
        responseDto.setNumero(5);
        responseDto.setCapacidad(4);
        responseDto.setEstado("DISPONIBLE");
        responseDto.setCuentaAbierta(false);
    }

    @Test
    @DisplayName("✅ Crear mesa válida - debe retornar ResponseDTO con id")
    void crear_mesaValida_debeRetornarResponseDTO() {
        when(mapperIn.toDomain(requestDto)).thenReturn(mesaEntidad);
        when(mapperOut.toDTO(any(Mesa.class))).thenReturn(responseDto);

        MesaResponseDTO resultado = mesaService.crearMesa(requestDto);

        assertNotNull(resultado);
        assertEquals(5, resultado.getNumero());
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(mapperIn, times(1)).toDomain(requestDto);
        verify(mapperOut, times(1)).toDTO(any(Mesa.class));
    }

    @Test
    @DisplayName("❌ Crear mesa duplicada - debe lanzar excepción")
    void crear_mesaDuplicada_lanzarExcepcion() {
        when(mapperIn.toDomain(requestDto)).thenReturn(mesaEntidad);
        when(mapperOut.toDTO(any(Mesa.class))).thenReturn(responseDto);
        mesaService.crearMesa(requestDto);

        MesaAlreadyExistsException ex = assertThrows(
                MesaAlreadyExistsException.class,
                () -> mesaService.crearMesa(requestDto)
        );

        assertTrue(ex.getMessage().contains("5"));
    }

    @Test
    @DisplayName("✅ Obtener disponibles - retorna solo disponibles")
    void obtenerDisponibles() {
        when(mapperIn.toDomain(requestDto)).thenReturn(mesaEntidad);
        when(mapperOut.toDTO(any(Mesa.class))).thenReturn(responseDto);
        mesaService.crearMesa(requestDto);

        List<MesaResponseDTO> disponibles = mesaService.obtenerDisponibles();

        assertEquals(1, disponibles.size());
    }
}
