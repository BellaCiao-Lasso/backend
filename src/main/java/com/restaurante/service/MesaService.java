package com.restaurante.service;

import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;

import java.util.List;

public interface MesaService {
    MesaResponseDTO crearMesa(MesaRequestDTO requestDTO);
    List<MesaResponseDTO> obtenerTodas();
    List<MesaResponseDTO> obtenerDisponibles();
    MesaResponseDTO cambiarEstado(Long id, String nuevoEstado);
    MesaResponseDTO abrirCuenta(Long id);
    MesaResponseDTO cerrarCuenta(Long id);
}
