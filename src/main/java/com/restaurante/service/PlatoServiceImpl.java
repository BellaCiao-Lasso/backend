package com.restaurante.service;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.mapper.PlatoMapperOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoMapperOut mapperOut;
    
    // Base de datos en memoria
    private final Map<Long, Plato> platos = new ConcurrentHashMap<>();
    private final AtomicLong secuencias = new AtomicLong(1);

    @Override
    public List<PlatoResponseDTO> obtenerTodos() {
        return platos.values().stream()
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<PlatoResponseDTO> obtenerDisponibles() {
        return platos.values().stream()
                .filter(Plato::getDisponible) // Lambda para Boolean
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<PlatoResponseDTO> obtenerPorCategoria(String categoria) {
        return platos.values().stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria))
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public PlatoResponseDTO obtenerPorId(Long id) {
        Plato plato = platos.get(id);
        if (plato == null) {
            throw new RuntimeException("Plato no encontrado con ID: " + id);
        }
        return mapperOut.toDTO(plato);
    }

    @Override
    public PlatoResponseDTO crearPlato(Plato plato) {
        if (!plato.esValido()) {
            throw new RuntimeException("El plato no es válido. Faltan datos requeridos.");
        }
        plato.setId(secuencias.getAndIncrement());
        platos.put(plato.getId(), plato);
        return mapperOut.toDTO(plato);
    }

    @Override
    public PlatoResponseDTO actualizarPlato(Long id, Plato platoActualizado) {
        Plato platoExistente = platos.get(id);
        if (platoExistente == null) {
            throw new RuntimeException("Plato no encontrado con ID: " + id);
        }
        platoExistente.setNombre(platoActualizado.getNombre());
        platoExistente.setPrecio(platoActualizado.getPrecio());
        platoExistente.setCategoria(platoActualizado.getCategoria());
        platoExistente.setDisponible(platoActualizado.getDisponible());
        
        platos.put(id, platoExistente);
        return mapperOut.toDTO(platoExistente);
    }

    @Override
    public void eliminarPlato(Long id) {
        platos.remove(id);
    }

    @Override
    public boolean existeYEstaDisponible(Long idPlato) {
        Plato plato = platos.get(idPlato);
        return plato != null && Boolean.TRUE.equals(plato.getDisponible());
    }
}
