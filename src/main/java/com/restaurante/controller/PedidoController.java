package com.restaurante.controller;

import com.restaurante.controller.docs.PedidoApi;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController implements PedidoApi {

    private final PedidoService pedidoService;

    @Override
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO requestDTO) {
        log.info("POST /api/v1/pedidos - Creando pedido para mesa {}", requestDTO.getIdMesa());
        PedidoResponseDTO respuesta = pedidoService.crearPedido(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Override
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa) {
        log.info("GET /api/v1/pedidos/mesa/{} - Consultando pedidos", idMesa);
        return ResponseEntity.ok(pedidoService.obtenerPorMesa(idMesa));
    }

    @Override
    @PostMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("POST /api/v1/pedidos/{}/estado - Nuevo estado: {}", id, estado);
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }
}
