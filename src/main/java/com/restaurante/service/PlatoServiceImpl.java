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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository platoRepository;
    private final PlatoEntityMapper entityMapper;
    private final PlatoMapperOut mapperOut;

    @Override
    public List<PlatoResponseDTO> obtenerTodos() {
        log.info("Consultando todos los platos en BD");
        return platoRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<PlatoResponseDTO> obtenerDisponibles() {
        log.info("Buscando platos disponibles en BD");
        return platoRepository.findByDisponibleTrue().stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<PlatoResponseDTO> obtenerPorCategoria(String categoria) {
        log.info("Filtrando platos por categoría: '{}'", categoria);
        return platoRepository.findByCategoriaIgnoreCase(categoria).stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public PlatoResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando plato con id={}", id);
        PlatoEntity entity = buscarOLanzar(id);
        Plato plato = entityMapper.toDomain(entity);
        return mapperOut.toDTO(plato);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlatoResponseDTO crearPlato(Plato plato) {
        log.info("Creando plato con nombre '{}'", plato.getNombre());

        if (platoRepository.existsByNombreIgnoreCase(plato.getNombre())) {
            log.warn("Intento de crear plato duplicado: '{}'", plato.getNombre());
            throw new PlatoAlreadyExistsException("Ya existe un plato con ese nombre: " + plato.getNombre());
        }

        if (!Boolean.TRUE.equals(plato.esValido())) {
            log.error("Plato inválido. Faltan datos requeridos: {}", plato);
            throw new ReglaDeNegocioException("El plato no es válido. Faltan datos requeridos.");
        }

        if (plato.getDisponible() == null) {
            plato.setDisponible(true);
        }

        PlatoEntity entity = entityMapper.toEntity(plato);
        entity = platoRepository.save(entity);

        Plato platoGuardado = entityMapper.toDomain(entity);
        log.info("Plato '{}' creado en BD con id={}", platoGuardado.getNombre(), platoGuardado.getId());
        return mapperOut.toDTO(platoGuardado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlatoResponseDTO actualizarPlato(Long id, Plato platoActualizado) {
        log.info("Actualizando plato con ID: {}", id);
        PlatoEntity entityExistente = buscarOLanzar(id);

        // Verificar conflicto de nombre (si está cambiando el nombre y el nuevo ya existe)
        if (!entityExistente.getNombre().equalsIgnoreCase(platoActualizado.getNombre())
                && platoRepository.existsByNombreIgnoreCase(platoActualizado.getNombre())) {
            throw new PlatoAlreadyExistsException("Ya existe un plato con ese nombre: " + platoActualizado.getNombre());
        }

        entityExistente.setNombre(platoActualizado.getNombre());
        entityExistente.setPrecio(platoActualizado.getPrecio());
        entityExistente.setCategoria(platoActualizado.getCategoria());
        if (platoActualizado.getDescripcion() != null) {
            entityExistente.setDescripcion(platoActualizado.getDescripcion());
        }
        if (platoActualizado.getDisponible() != null) {
            entityExistente.setDisponible(platoActualizado.getDisponible());
        }

        entityExistente = platoRepository.save(entityExistente);
        log.info("Plato con ID {} actualizado correctamente en BD", id);
        return mapperOut.toDTO(entityMapper.toDomain(entityExistente));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlatoResponseDTO actualizarDisponibilidad(Long id, boolean disponible) {
        log.info("Cambiando disponibilidad del plato ID {} a {}", id, disponible);
        PlatoEntity entityExistente = buscarOLanzar(id);
        entityExistente.setDisponible(disponible);
        entityExistente = platoRepository.save(entityExistente);
        log.info("Disponibilidad del plato ID {} actualizada a {}", id, disponible);
        return mapperOut.toDTO(entityMapper.toDomain(entityExistente));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarPlato(Long id) {
        log.warn("Eliminando plato con ID: {}", id);
        PlatoEntity entity = buscarOLanzar(id);
        platoRepository.delete(entity);
        log.info("Plato con ID {} eliminado de la BD", id);
    }

    @Override
    public boolean existeYEstaDisponible(Long idPlato) {
        return platoRepository.findById(idPlato)
                .map(PlatoEntity::getDisponible)
                .orElse(false);
    }

    @Override
    public PlatoResponseDTO buscarPorNombre(String nombre) {
        log.info("Buscando plato por nombre: '{}' usando @Query JPQL", nombre);
        PlatoEntity entity = platoRepository.buscarPorNombre(nombre)
                .orElseThrow(() -> new PlatoNotFoundException("Plato no encontrado con nombre: " + nombre));
        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    private PlatoEntity buscarOLanzar(Long id) {
        return platoRepository.findById(id)
                .orElseThrow(() -> new PlatoNotFoundException("Plato no encontrado con id: " + id));
    }
}
