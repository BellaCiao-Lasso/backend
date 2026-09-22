package com.restaurante.service;

import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO);
    List<PedidoResponseDTO> obtenerPorMesa(Long idMesa);
    PedidoResponseDTO actualizarEstado(Long idPedido, String nuevoEstado);
}
