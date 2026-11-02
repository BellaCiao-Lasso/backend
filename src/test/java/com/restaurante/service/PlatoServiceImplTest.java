package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.exception.ReglaDeNegocioException;
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
class PlatoServiceImplTest {

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private PlatoEntityMapper entityMapper;

    @Mock
    private PlatoMapperOut mapperOut;

    @InjectMocks
    private PlatoServiceImpl platoService;

    private PlatoEntity entity;
    private Plato domain;
    private PlatoResponseDTO dto;

    @BeforeEach
    void setUp() {
        domain = new Plato(1L, "Pizza Margherita", 25000.0, "PLATO_FUERTE", "Con albahaca", true);
        entity = PlatoEntity.builder()
                .id(1L)
                .nombre("Pizza Margherita")
                .precio(25000.0)
                .categoria("PLATO_FUERTE")
                .descripcion("Con albahaca")
                .disponible(true)
                .build();
        dto = new PlatoResponseDTO();
        dto.setId(1L);
        dto.setNombre("Pizza Margherita");
        dto.setPrecio(25000.0);
        dto.setCategoria("PLATO_FUERTE");
        dto.setDisponible(true);
    }

    @Test
    @DisplayName("❌ Crear plato con datos inválidos (precio negativo) lanza ReglaDeNegocioException (422)")
    void crearPlato_datosInvalidos_lanzaReglaDeNegocioException() {
        // Arrange
        Plato invalido = new Plato(null, "", -10.0, "PLATO_FUERTE", true);
        when(platoRepository.existsByNombreIgnoreCase("")).thenReturn(false);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> platoService.crearPlato(invalido));
    }

    @Test
    @DisplayName("✅ Actualizar plato existente con datos válidos")
    void actualizarPlato_valido_retornaDTO() {
        // Arrange
        Plato actualizado = new Plato(1L, "Pizza Napoletana", 27000.0, "PLATO_FUERTE", "Masa madre", true);
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(platoRepository.existsByNombreIgnoreCase("Pizza Napoletana")).thenReturn(false);
        when(platoRepository.save(any(PlatoEntity.class))).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(actualizado);
        when(mapperOut.toDTO(actualizado)).thenReturn(dto);

        // Act
        PlatoResponseDTO resultado = platoService.actualizarPlato(1L, actualizado);

        // Assert
        assertNotNull(resultado);
        verify(platoRepository).save(entity);
    }

    @Test
    @DisplayName("❌ Actualizar plato con nombre ya registrado en otro plato lanza PlatoAlreadyExistsException (409)")
    void actualizarPlato_nombreEnConflicto_lanzaExcepcion() {
        // Arrange
        Plato actualizado = new Plato(1L, "Lasagna", 27000.0, "PLATO_FUERTE", "Masa madre", true);
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(platoRepository.existsByNombreIgnoreCase("Lasagna")).thenReturn(true);

        // Act & Assert
        assertThrows(PlatoAlreadyExistsException.class, () -> platoService.actualizarPlato(1L, actualizado));
    }

    @Test
    @DisplayName("✅ Eliminar plato existente invoca delete en el repositorio")
    void eliminarPlato_existente_eliminaDeBD() {
        // Arrange
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        platoService.eliminarPlato(1L);

        // Assert
        verify(platoRepository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("❌ Eliminar plato inexistente lanza PlatoNotFoundException (404)")
    void eliminarPlato_inexistente_lanzaPlatoNotFoundException() {
        // Arrange
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PlatoNotFoundException.class, () -> platoService.eliminarPlato(99L));
    }

    @Test
    @DisplayName("✅ Filtrar por categoría y verificar disponibilidad")
    void obtenerPorCategoriaYDisponibilidad() {
        // Arrange
        when(platoRepository.findByCategoriaIgnoreCase("PLATO_FUERTE")).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);
        when(mapperOut.toDTO(domain)).thenReturn(dto);
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        List<PlatoResponseDTO> porCategoria = platoService.obtenerPorCategoria("PLATO_FUERTE");
        boolean disponible = platoService.existeYEstaDisponible(1L);

        // Assert
        assertEquals(1, porCategoria.size());
        assertTrue(disponible);
    }
}
