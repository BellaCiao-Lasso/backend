package com.restaurante.exception;

import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/platos");
    }

    @Test
    @DisplayName("400 Bad Request - MethodArgumentNotValidException concatena errores de campo")
    void handleValidacion_debeRetornar400() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("plato", "nombre", "El nombre es obligatorio"),
                new FieldError("plato", "precio", "Debe ser mayor a cero")
        ));

        // Act
        ErrorResponseDTO response = handler.handleValidacion(ex, request);

        // Assert
        assertEquals(400, response.status());
        assertEquals("Datos de entrada inválidos", response.error());
        assertTrue(response.message().contains("nombre: El nombre es obligatorio"));
        assertTrue(response.message().contains("precio: Debe ser mayor a cero"));
        assertEquals("/api/v1/platos", response.path());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("400 Bad Request - HttpMessageNotReadableException retorna formato inválido")
    void handleFormatoInvalido_debeRetornar400() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        // Act
        ErrorResponseDTO response = handler.handleFormatoInvalido(ex, request);

        // Assert
        assertEquals(400, response.status());
        assertEquals("Petición mal formada", response.error());
        assertEquals("/api/v1/platos", response.path());
    }

    @Test
    @DisplayName("401 Unauthorized - AuthenticationException retorna error de autenticación")
    void handleAuthentication_debeRetornar401() {
        // Arrange
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        // Act
        ErrorResponseDTO response = handler.handleAuthentication(ex, request);

        // Assert
        assertEquals(401, response.status());
        assertEquals("No autorizado", response.error());
    }

    @Test
    @DisplayName("403 Forbidden - AccessDeniedException retorna acceso denegado")
    void handleAccessDenied_debeRetornar403() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        // Act
        ErrorResponseDTO response = handler.handleAccessDenied(ex, request);

        // Assert
        assertEquals(403, response.status());
        assertEquals("Acceso Denegado", response.error());
    }

    @Test
    @DisplayName("404 Not Found - PlatoNotFoundException / MesaNotFoundException / PedidoNotFoundException")
    void handleNoEncontrado_debeRetornar404() {
        // Arrange
        PlatoNotFoundException ex = new PlatoNotFoundException("Plato no encontrado con id: 99");

        // Act
        ErrorResponseDTO response = handler.handleNoEncontrado(ex, request);

        // Assert
        assertEquals(404, response.status());
        assertEquals("No encontrado", response.error());
        assertEquals("Plato no encontrado con id: 99", response.message());
    }

    @Test
    @DisplayName("409 Conflict - PlatoAlreadyExistsException / MesaAlreadyExistsException")
    void handleConflicto_debeRetornar409() {
        // Arrange
        PlatoAlreadyExistsException ex = new PlatoAlreadyExistsException("Ya existe el plato Pizza");

        // Act
        ErrorResponseDTO response = handler.handleConflicto(ex, request);

        // Assert
        assertEquals(409, response.status());
        assertEquals("Conflicto", response.error());
        assertEquals("Ya existe el plato Pizza", response.message());
    }

    @Test
    @DisplayName("422 Unprocessable Entity - EstadoInvalidoException / ReglaDeNegocioException / PedidoNoModificableException")
    void handleEstadoInvalido_debeRetornar422() {
        // Arrange
        EstadoInvalidoException ex1 = new EstadoInvalidoException("La mesa no está ocupada");
        PedidoNoModificableException ex2 = new PedidoNoModificableException("El pedido ya no se puede modificar");

        // Act
        ErrorResponseDTO res1 = handler.handleEstadoInvalido(ex1, request);
        ErrorResponseDTO res2 = handler.handleEstadoInvalido(ex2, request);

        // Assert
        assertEquals(422, res1.status());
        assertEquals("La mesa no está ocupada", res1.message());
        assertEquals(422, res2.status());
        assertEquals("El pedido ya no se puede modificar", res2.message());
    }

    @Test
    @DisplayName("500 Internal Server Error - Exception oculta detalles sensibles al cliente")
    void handleGeneral_debeRetornar500SinExponerMensajeInterno() {
        // Arrange
        RuntimeException ex = new RuntimeException("Select * from secret_table failed");

        // Act
        ErrorResponseDTO response = handler.handleGeneral(ex, request);

        // Assert
        assertEquals(500, response.status());
        assertEquals("Error interno", response.error());
        assertEquals("Algo salió mal, intenta de nuevo", response.message());
        assertFalse(response.message().contains("secret_table"));
    }
}
