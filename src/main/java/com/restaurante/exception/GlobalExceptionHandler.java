package com.restaurante.exception;

import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    // Cuerpo JSON mal formado o tipo de argumento inválido -> 400
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleFormatoInvalido(Exception ex, HttpServletRequest request) {
        log.warn("Formato de petición inválido en {}: {}", request.getRequestURI(), ex.getMessage());
        return ErrorResponseDTO.of(400, "Petición mal formada", "El formato de los datos enviados no es válido", request.getRequestURI());
    }

    // Error de autenticación -> 401
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Fallo de autenticación (401) en {}: {}", request.getRequestURI(), ex.getMessage());
        return ErrorResponseDTO.of(401, "No autorizado", "Credenciales inválidas o sesión no autenticada", request.getRequestURI());
    }

    // Recurso no encontrado -> 404
    @ExceptionHandler({RecursoNoEncontradoException.class, PlatoNotFoundException.class, MesaNotFoundException.class, PedidoNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNoEncontrado(RuntimeException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ErrorResponseDTO.of(404, "No encontrado", ex.getMessage(), request.getRequestURI());
    }

    // Conflicto de negocio -> 409
    @ExceptionHandler({ConflictoException.class, PlatoAlreadyExistsException.class, MesaAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleConflicto(RuntimeException ex, HttpServletRequest request) {
        log.warn("Conflicto de negocio: {}", ex.getMessage());
        return ErrorResponseDTO.of(409, "Conflicto", ex.getMessage(), request.getRequestURI());
    }

    // Estado o regla de negocio inválida -> 422
    @ExceptionHandler({EstadoInvalidoException.class, ReglaDeNegocioException.class, PedidoNoModificableException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handleEstadoInvalido(RuntimeException ex, HttpServletRequest request) {
        log.warn("Regla o estado de negocio inválido: {}", ex.getMessage());
        return ErrorResponseDTO.of(422, "Estado inválido", ex.getMessage(), request.getRequestURI());
    }

    // Acceso denegado (Autenticado pero sin permisos) -> 403
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
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
