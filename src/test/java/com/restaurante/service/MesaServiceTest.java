package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.mapper.MesaMapperIn;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.repository.MesaRepository;
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
class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;
    
    @Mock
    private MesaEntityMapper entityMapper;

    @Mock
    private MesaMapperIn mapperIn;

    @Mock
    private MesaMapperOut mapperOut;

    @InjectMocks
    private MesaServiceImpl mesaService;

    private MesaRequestDTO requestDto;
    private Mesa mesaDomain;
    private MesaEntity mesaEntity;
    private MesaResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new MesaRequestDTO();
        requestDto.setNumero(5);
        requestDto.setCapacidad(4);

        mesaDomain = new Mesa(1L, 5, 4, EstadoMesa.DISPONIBLE, false);
        
        mesaEntity = new MesaEntity();
        mesaEntity.setId(1L);
        mesaEntity.setNumero(5);
        mesaEntity.setCapacidad(4);
        mesaEntity.setEstado("DISPONIBLE");
        mesaEntity.setCuentaAbierta(false);

        responseDto = new MesaResponseDTO();
        responseDto.setId(1L);
        responseDto.setNumero(5);
        responseDto.setCapacidad(4);
        responseDto.setEstado("DISPONIBLE");
    }

    @Test
    @DisplayName("✅ Crear mesa válida - debe retornar ResponseDTO")
    void crear_mesaValida_debeRetornarResponseDTO() {
        when(mesaRepository.existsByNumero(5)).thenReturn(false);
        when(mapperIn.toDomain(any())).thenReturn(mesaDomain);
        when(entityMapper.toEntity(any())).thenReturn(mesaEntity);
        when(mesaRepository.save(any())).thenReturn(mesaEntity);
        when(entityMapper.toDomain(any())).thenReturn(mesaDomain);
        when(mapperOut.toDTO(any())).thenReturn(responseDto);

        MesaResponseDTO resultado = mesaService.crearMesa(requestDto);

        assertNotNull(resultado);
        assertEquals(5, resultado.getNumero());
    }

    @Test
    @DisplayName("❌ Crear mesa con número duplicado - debe lanzar MesaAlreadyExistsException")
    void crear_mesaDuplicada_lanzarExcepcion() {
        when(mesaRepository.existsByNumero(5)).thenReturn(true);

        assertThrows(MesaAlreadyExistsException.class, () -> mesaService.crearMesa(requestDto));
    }

    @Test
    @DisplayName("✅ Cambiar estado de la mesa")
    void cambiarEstado_mesaValida() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaEntity));
        when(entityMapper.toDomain(any())).thenReturn(mesaDomain);
        when(entityMapper.toEntity(any())).thenReturn(mesaEntity);
        when(mesaRepository.save(any())).thenReturn(mesaEntity);
        when(mapperOut.toDTO(any())).thenReturn(responseDto);

        MesaResponseDTO resultado = mesaService.cambiarEstado(1L, "OCUPADA");

        assertNotNull(resultado);
    }
    
    @Test
    @DisplayName("✅ Obtener mesas disponibles")
    void obtenerDisponibles() {
        when(mesaRepository.findByEstado("DISPONIBLE")).thenReturn(List.of(mesaEntity));
        when(entityMapper.toDomain(any())).thenReturn(mesaDomain);
        when(mapperOut.toDTO(any())).thenReturn(responseDto);
        
        List<MesaResponseDTO> mesas = mesaService.obtenerDisponibles();
        
        assertEquals(1, mesas.size());
    }
}
