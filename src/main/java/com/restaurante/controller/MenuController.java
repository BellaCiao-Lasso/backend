package com.restaurante.controller;

import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menú", description = "Consulta del menú disponible para los clientes")
public class MenuController {

    private final PlatoService platoService;

    @Operation(summary = "Obtener menú completo", description = "Devuelve todos los platos disponibles actualmente en el menú")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menú obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> obtenerMenuCompleto() {
        log.info("GET /api/v1/menu - Solicitando menú completo de platos disponibles");
        List<PlatoResponseDTO> disponibles = platoService.obtenerDisponibles();
        log.info("Menú disponible: {} platos encontrados", disponibles.size());
        return ResponseEntity.ok(disponibles);
    }

    @Operation(summary = "Filtrar menú por categoría", description = "Devuelve los platos disponibles filtrados por categoría (ej: entrada, principal, postre)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Platos filtrados correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron platos para esa categoría")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PlatoResponseDTO>> obtenerMenuPorCategoria(@PathVariable String categoria) {
        log.info("GET /api/v1/menu/categoria/{} - Filtrando menú por categoría", categoria);
        List<PlatoResponseDTO> filtrados = platoService.obtenerPorCategoria(categoria);
        log.info("Se encontraron {} platos en la categoría '{}'", filtrados.size(), categoria);
        return ResponseEntity.ok(filtrados);
    }
}


