package com.restaurante.exception;

import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Excepciones de input (Bean Validation) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        log.warn("Validación de input fallida: {}", errores);
        return ErrorResponseDTO.of(400, "Datos de entrada inválidos", errores, request.getRequestURI());
    }

    // Recurso no encontrado -> 404
    @ExceptionHandler({PlatoNotFoundException.class, MesaNotFoundException.class, PedidoNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNoEncontrado(RuntimeException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ErrorResponseDTO.of(404, "No encontrado", ex.getMessage(), request.getRequestURI());
    }

    // Conflicto de negocio -> 409
    @ExceptionHandler({PlatoAlreadyExistsException.class, MesaAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleConflicto(RuntimeException ex, HttpServletRequest request) {
        log.warn("Conflicto de negocio: {}", ex.getMessage());
        return ErrorResponseDTO.of(409, "Conflicto", ex.getMessage(), request.getRequestURI());
    }

    // Estado inválido de negocio -> 422
    @ExceptionHandler(EstadoInvalidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handleEstadoInvalido(EstadoInvalidoException ex, HttpServletRequest request) {
        log.warn("Estado de negocio inválido: {}", ex.getMessage());
        return ErrorResponseDTO.of(422, "Estado inválido", ex.getMessage(), request.getRequestURI());
    }

    // Acceso denegado (Autenticado pero sin permisos) -> 403
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Intento de acceso denegado (403) a {}: {}", request.getRequestURI(), ex.getMessage());
        return ErrorResponseDTO.of(403, "Acceso Denegado", "No tienes permisos para realizar esta acción", request.getRequestURI());
    }

    // Cualquier otro error no controlado -> 500
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex); // full stack trace en ERROR
        return ErrorResponseDTO.of(500, "Error interno", "Algo salió mal, intenta de nuevo", request.getRequestURI());
        // NUNCA expongas ex.getMessage() aquí - puede filtrar info sensible
    }
}
