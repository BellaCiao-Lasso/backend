package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.mapper.PlatoMapperOut;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.persistence.repository.PlatoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoServiceTest {

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private PlatoEntityMapper entityMapper;

    @Mock
    private PlatoMapperOut mapperOut;

    @InjectMocks
    private PlatoServiceImpl platoService;

    private Plato platoDomain;
    private PlatoEntity platoEntity;
    private PlatoResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        platoDomain = new Plato(1L, "Tacos", 15000.0, "PLATO_FUERTE", true);
        platoEntity = new PlatoEntity();
        platoEntity.setId(1L);
        platoEntity.setNombre("Tacos");
        platoEntity.setPrecio(15000.0);
        platoEntity.setCategoria("PLATO_FUERTE");
        platoEntity.setDisponible(true);

        responseDto = new PlatoResponseDTO();
        responseDto.setId(1L);
        responseDto.setNombre("Tacos");
        responseDto.setPrecio(15000.0);
        responseDto.setCategoria("PLATO_FUERTE");
        responseDto.setDisponible(true);
    }

    @Test
    @DisplayName("✅ Crear plato válido - debe retornar ResponseDTO con id asignado")
    void crear_platoValido_debeRetornarResponseDTO() {
        // Arrange
        when(platoRepository.existsByNombreIgnoreCase(any())).thenReturn(false);
        when(entityMapper.toEntity(any())).thenReturn(platoEntity);
        when(platoRepository.save(any())).thenReturn(platoEntity);
        when(entityMapper.toDomain(any())).thenReturn(platoDomain);
        when(mapperOut.toDTO(any(Plato.class))).thenReturn(responseDto);

        // Act
        PlatoResponseDTO resultado = platoService.crearPlato(platoDomain);

        // Assert
        assertNotNull(resultado);
        assertEquals("Tacos", resultado.getNombre());
        assertEquals(15000.0, resultado.getPrecio());
    }

    @Test
    @DisplayName("❌ Crear plato con nombre duplicado - debe lanzar PlatoAlreadyExistsException")
    void crear_nombreDuplicado_debeLanzarExcepcion() {
        // Arrange
        when(platoRepository.existsByNombreIgnoreCase(any())).thenReturn(true);

        // Act & Assert
        PlatoAlreadyExistsException ex = assertThrows(
                PlatoAlreadyExistsException.class,
                () -> platoService.crearPlato(platoDomain)
        );

        assertTrue(ex.getMessage().contains("Tacos"));
    }

    @Test
    @DisplayName("❌ Obtener plato por ID inexistente - debe lanzar PlatoNotFoundException")
    void obtenerPorId_noExistente_debeLanzarExcepcion() {
        // Arrange
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                PlatoNotFoundException.class,
                () -> platoService.obtenerPorId(99L)
        );
    }

    @Test
    @DisplayName("✅ Obtener platos disponibles - debe retornar solo los disponibles")
    void obtenerDisponibles_debeRetornarSoloDisponibles() {
        // Arrange
        when(platoRepository.findByDisponibleTrue()).thenReturn(List.of(platoEntity));
        when(entityMapper.toDomain(any())).thenReturn(platoDomain);
        when(mapperOut.toDTO(any())).thenReturn(responseDto);

        // Act
        List<PlatoResponseDTO> disponibles = platoService.obtenerDisponibles();

        // Assert
        assertEquals(1, disponibles.size());
    }

    @Test
    @DisplayName("✅ Actualizar disponibilidad de un plato - debe cambiar disponible y retornar DTO")
    void actualizarDisponibilidad_debeRetornarDTOCorrecto() {
        // Arrange
        when(platoRepository.findById(1L)).thenReturn(Optional.of(platoEntity));
        when(platoRepository.save(any(PlatoEntity.class))).thenReturn(platoEntity);
        when(entityMapper.toDomain(any(PlatoEntity.class))).thenReturn(platoDomain);
        when(mapperOut.toDTO(any(Plato.class))).thenReturn(responseDto);

        // Act
        PlatoResponseDTO resultado = platoService.actualizarDisponibilidad(1L, false);

        // Assert
        assertNotNull(resultado);
        verify(platoRepository, times(1)).save(platoEntity);
    }

    @Test
    @DisplayName("✅ Buscar plato por nombre usando @Query JPQL - debe retornar DTO")
    void buscarPorNombre_debeRetornarDTOCorrecto() {
        // Arrange
        when(platoRepository.buscarPorNombre("Tacos")).thenReturn(Optional.of(platoEntity));
        when(entityMapper.toDomain(any(PlatoEntity.class))).thenReturn(platoDomain);
        when(mapperOut.toDTO(any(Plato.class))).thenReturn(responseDto);

        // Act
        PlatoResponseDTO resultado = platoService.buscarPorNombre("Tacos");

        // Assert
        assertNotNull(resultado);
        assertEquals("Tacos", resultado.getNombre());
        verify(platoRepository, times(1)).buscarPorNombre("Tacos");
    }
}
