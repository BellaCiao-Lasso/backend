package com.restaurante.controller;

import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de los pedidos de las mesas")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Crear un nuevo pedido", description = "Registra un pedido para una mesa ocupada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "422", description = "Mesa no ocupada o plato no disponible")
    })
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO requestDTO) {
        log.info("POST /api/v1/pedidos - Creando pedido para mesa {}", requestDTO.getIdMesa());
        PedidoResponseDTO respuesta = pedidoService.crearPedido(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Obtener pedidos por mesa", description = "Devuelve todos los pedidos de una mesa en particular")
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa) {
        log.info("GET /api/v1/pedidos/mesa/{} - Consultando pedidos", idMesa);
        return ResponseEntity.ok(pedidoService.obtenerPorMesa(idMesa));
    }

    @Operation(summary = "Actualizar estado de un pedido", description = "Cambia el estado de un pedido (ej: PREPARANDO, LISTO, ENTREGADO)")
    @PostMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("POST /api/v1/pedidos/{}/estado - Nuevo estado: {}", id, estado);
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }
}
