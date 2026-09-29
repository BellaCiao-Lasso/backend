package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.persistence.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validador de platos que inyecta directamente el Repositorio.
 * Como se explica en la diapositiva: ya no recibe colecciones en memoria,
 * sino que delega la verificación al motor de base de datos con existsByNombreIgnoreCase().
 */
@Component
@RequiredArgsConstructor
public class PlatoValidator {

    private final PlatoRepository platoRepository;

    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new PlatoAlreadyExistsException("Ya existe un plato con ese nombre: " + nombre);
        }
    }

    public void validarNombreUnicoParaActualizar(String nombre, Long id) {
        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new PlatoAlreadyExistsException("Ya existe un plato con ese nombre: " + nombre);
        }
    }
}
