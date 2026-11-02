package com.restaurante.controller.docs;

import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Pedidos", description = "Gestión de los pedidos de las mesas")
public interface PedidoApi {

    @Operation(summary = "Crear un nuevo pedido", description = "Registra un pedido para una mesa ocupada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "422", description = "Mesa no ocupada o plato no disponible")
    })
    ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO requestDTO);

    @Operation(summary = "Obtener pedidos por mesa", description = "Devuelve todos los pedidos de una mesa en particular")
    ResponseEntity<List<PedidoResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa);

    @Operation(summary = "Actualizar estado de un pedido", description = "Cambia el estado de un pedido (ej: EN_PREPARACION, LISTO, ENTREGADO, CANCELADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    ResponseEntity<PedidoResponseDTO> actualizarEstado(@PathVariable Long id, @RequestParam String estado);
}

