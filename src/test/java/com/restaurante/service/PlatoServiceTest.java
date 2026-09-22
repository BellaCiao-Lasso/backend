package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoMapperOut;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
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
class PlatoServiceTest {

    @Mock
    private PlatoMapperOut mapperOut;

    @InjectMocks
    private PlatoServiceImpl platoService;

    private Plato platoEntidad;
    private PlatoResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        platoEntidad = new Plato(null, "Tacos", 15000.0, "PLATO_FUERTE", true);
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
        // GIVEN
        when(mapperOut.toDTO(any(Plato.class))).thenReturn(responseDto);

        // WHEN
        PlatoResponseDTO resultado = platoService.crearPlato(platoEntidad);

        // THEN
        assertNotNull(resultado);
        assertEquals("Tacos", resultado.getNombre());
        assertEquals(15000.0, resultado.getPrecio());
        verify(mapperOut, times(1)).toDTO(any(Plato.class));
    }

    @Test
    @DisplayName("❌ Crear plato con nombre duplicado - debe lanzar PlatoAlreadyExistsException")
    void crear_nombreDuplicado_debeLanzarExcepcion() {
        // GIVEN - primero creamos un plato
        when(mapperOut.toDTO(any(Plato.class))).thenReturn(responseDto);
        platoService.crearPlato(platoEntidad);

        // WHEN & THEN - el segundo intento debe lanzar la excepción
        Plato platoDuplicado = new Plato(null, "Tacos", 20000.0, "PLATO_FUERTE", true);

        PlatoAlreadyExistsException ex = assertThrows(
                PlatoAlreadyExistsException.class,
                () -> platoService.crearPlato(platoDuplicado)
        );

        assertTrue(ex.getMessage().contains("Tacos"));
    }

    @Test
    @DisplayName("❌ Obtener plato por ID inexistente - debe lanzar PlatoNotFoundException")
    void obtenerPorId_noExistente_debeLanzarExcepcion() {
        // WHEN & THEN
        assertThrows(
                PlatoNotFoundException.class,
                () -> platoService.obtenerPorId(99L)
        );
    }

    @Test
    @DisplayName("✅ Obtener platos disponibles - debe retornar solo los disponibles")
    void obtenerDisponibles_debeRetornarSoloDisponibles() {
        // GIVEN
        when(mapperOut.toDTO(any(Plato.class))).thenReturn(responseDto);
        platoService.crearPlato(platoEntidad);
        
        Plato platoNoDisponible = new Plato(null, "Pizza", 25000.0, "PLATO_FUERTE", false);
        platoService.crearPlato(platoNoDisponible);

        // WHEN
        List<PlatoResponseDTO> disponibles = platoService.obtenerDisponibles();

        // THEN
        assertEquals(1, disponibles.size());
    }
}
