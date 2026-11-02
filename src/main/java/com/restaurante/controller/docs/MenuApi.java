package com.restaurante.controller.docs;

import com.restaurante.model.dto.response.PlatoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Menú", description = "Consulta del menú disponible para los clientes")
public interface MenuApi {

    @Operation(summary = "Obtener menú completo", description = "Devuelve todos los platos disponibles actualmente en el menú")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menú obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<List<PlatoResponseDTO>> obtenerMenuCompleto();

    @Operation(summary = "Filtrar menú por categoría", description = "Devuelve los platos disponibles filtrados por categoría (ej: entrada, principal, postre)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Platos filtrados correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron platos para esa categoría")
    })
    ResponseEntity<List<PlatoResponseDTO>> obtenerMenuPorCategoria(@PathVariable String categoria);
}
