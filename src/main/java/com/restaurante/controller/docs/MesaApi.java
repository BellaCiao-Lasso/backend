package com.restaurante.controller.docs;

import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Mesas", description = "Gestión de las mesas del restaurante")
public interface MesaApi {

    @Operation(summary = "Crear nueva mesa", description = "Registra una nueva mesa con número único en el restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mesa creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. número menor a 1)"),
            @ApiResponse(responseCode = "409", description = "El número de mesa ya existe")
    })
    ResponseEntity<MesaResponseDTO> crearMesa(@Valid @RequestBody MesaRequestDTO requestDTO);

    @Operation(summary = "Listar todas las mesas", description = "Obtiene todas las mesas registradas con sus estados")
    ResponseEntity<List<MesaResponseDTO>> obtenerTodas();

    @Operation(summary = "Listar mesas disponibles", description = "Obtiene únicamente las mesas con estado DISPONIBLE")
    ResponseEntity<List<MesaResponseDTO>> obtenerDisponibles();

    @Operation(summary = "Abrir cuenta de una mesa", description = "Marca la cuenta como abierta y el estado pasa a OCUPADA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta abierta correctamente"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
            @ApiResponse(responseCode = "422", description = "La cuenta ya estaba abierta")
    })
    ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id);

    @Operation(summary = "Cerrar cuenta de una mesa", description = "Cierra la cuenta y marca la mesa como DISPONIBLE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta cerrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
            @ApiResponse(responseCode = "422", description = "La cuenta no estaba abierta")
    })
    ResponseEntity<MesaResponseDTO> cerrarCuenta(@PathVariable Long id);
}
