package com.restaurante.service;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.mapper.PlatoMapperOut;
import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoMapperOut mapperOut;
    
    // Base de datos en memoria
    private final Map<Long, Plato> platos = new ConcurrentHashMap<>();
    private final AtomicLong secuencias = new AtomicLong(1);

    @Override
    public List<PlatoResponseDTO> obtenerTodos() {
        log.info("Consultando todos los platos. Total en memoria: {}", platos.size());
        return platos.values().stream()
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<PlatoResponseDTO> obtenerDisponibles() {
        log.info("Filtrando platos disponibles del total de {}", platos.size());
        List<PlatoResponseDTO> disponibles = platos.values().stream()
                .filter(Plato::getDisponible)
                .map(mapperOut::toDTO)
                .toList();
        log.info("Platos disponibles encontrados: {}", disponibles.size());
        return disponibles;
    }

    @Override
    public List<PlatoResponseDTO> obtenerPorCategoria(String categoria) {
        log.info("Filtrando platos por categoría: '{}'", categoria);
        return platos.values().stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria))
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public PlatoResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando plato con id={}", id);
        Plato plato = buscarOLanzar(id);
        return mapperOut.toDTO(plato);
    }

    @Override
    public PlatoResponseDTO crearPlato(Plato plato) {
        log.info("Creando plato con nombre '{}'", plato.getNombre());
        
        if (nombreExiste(plato.getNombre())) {
            log.warn("Intento de crear plato duplicado: '{}'", plato.getNombre());
            throw new PlatoAlreadyExistsException("Ya existe un plato con ese nombre: " + plato.getNombre());
        }

        if (!plato.esValido()) {
            log.error("Plato inválido. Faltan datos requeridos: {}", plato);
            throw new RuntimeException("El plato no es válido. Faltan datos requeridos.");
        }
        
        plato.setId(secuencias.getAndIncrement());
        platos.put(plato.getId(), plato);
        
        log.info("Plato '{}' creado con id={}", plato.getNombre(), plato.getId());
        return mapperOut.toDTO(plato);
    }

    @Override
    public PlatoResponseDTO actualizarPlato(Long id, Plato platoActualizado) {
        log.info("Actualizando plato con ID: {}", id);
        Plato platoExistente = buscarOLanzar(id);
        platoExistente.setNombre(platoActualizado.getNombre());
        platoExistente.setPrecio(platoActualizado.getPrecio());
        platoExistente.setCategoria(platoActualizado.getCategoria());
        platoExistente.setDisponible(platoActualizado.getDisponible());
        
        platos.put(id, platoExistente);
        log.info("Plato con ID {} actualizado correctamente: '{}'", id, platoExistente.getNombre());
        return mapperOut.toDTO(platoExistente);
    }

    @Override
    public void eliminarPlato(Long id) {
        log.warn("Eliminando plato con ID: {}", id);
        platos.remove(id);
        log.info("Plato con ID {} eliminado. Total platos restantes: {}", id, platos.size());
    }

    @Override
    public boolean existeYEstaDisponible(Long idPlato) {
        Plato plato = platos.get(idPlato);
        boolean resultado = plato != null && Boolean.TRUE.equals(plato.getDisponible());
        log.debug("Verificación de disponibilidad para plato ID {}: {}", idPlato, resultado);
        return resultado;
    }

    // --- Verificar si nombre ya existe (devuelve boolean) -------------------
    private boolean nombreExiste(String nombre) {
        return platos.values().stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
    }

    // --- Buscar por id o lanzar excepción -------------------
    private Plato buscarOLanzar(Long id) {
        return Optional.ofNullable(platos.get(id))
                .orElseThrow(() -> new PlatoNotFoundException("Plato no encontrado con id: " + id));
    }
}

