package com.restaurante.service;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import java.util.List;

public interface PlatoService {
    List<PlatoResponseDTO> obtenerTodos();
    List<PlatoResponseDTO> obtenerDisponibles();
    List<PlatoResponseDTO> obtenerPorCategoria(String categoria);
    PlatoResponseDTO obtenerPorId(Long id);
    
    // Punto de entrada: Objeto de Dominio, NO DTO
    PlatoResponseDTO crearPlato(Plato plato);
    PlatoResponseDTO actualizarPlato(Long id, Plato plato);
    PlatoResponseDTO actualizarDisponibilidad(Long id, boolean disponible);
    
    void eliminarPlato(Long id);
    boolean existeYEstaDisponible(Long idPlato);
    PlatoResponseDTO buscarPorNombre(String nombre);
}
