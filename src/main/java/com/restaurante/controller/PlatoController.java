package com.restaurante.controller;


import com.restaurante.mapper.PlatoMapperIn;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Tag(name = "Platos v1", description = "Gestión de la carta del restaurante")
public class PlatoController {

    private final PlatoService platoService;
    private final PlatoMapperIn mapperIn; // El controller se encarga de transformar RequestDTO a Domain

    @Operation(summary = "Obtener todos los platos", description = "Devuelve la carta completa con estado de disponibilidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carta obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> listarPlatos() {
        log.info("GET /api/platos - Solicitando lista completa de platos");
        List<PlatoResponseDTO> platos = platoService.obtenerTodos();
        log.info("Se encontraron {} platos en la carta", platos.size());
        return ResponseEntity.ok(platos);
    }

    @Operation(summary = "Obtener un plato por ID", description = "Devuelve un plato específico buscándolo por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> obtenerPlato(@PathVariable Long id) {
        log.info("GET /api/platos/{} - Buscando plato por ID", id);
        return ResponseEntity.ok(platoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear un plato", description = "Agrega un nuevo plato a la carta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un plato con ese nombre")
    })
    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crearPlato(@RequestBody @Valid PlatoRequestDTO platoRequestDTO) {
        log.info("POST /api/platos - Creando plato: {}", platoRequestDTO.nombre());
        Plato platoDominio = mapperIn.toDomain(platoRequestDTO);
        PlatoResponseDTO respuesta = platoService.crearPlato(platoDominio);
        log.info("Plato creado exitosamente con ID: {}", respuesta.getId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un plato", description = "Modifica los datos de un plato existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> actualizarPlato(@PathVariable Long id, @RequestBody @Valid PlatoRequestDTO platoRequestDTO) {
        log.info("PUT /api/platos/{} - Actualizando plato", id);
        Plato platoDominio = mapperIn.toDomain(platoRequestDTO);
        PlatoResponseDTO respuesta = platoService.actualizarPlato(id, platoDominio);
        log.info("Plato con ID {} actualizado correctamente", id);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Eliminar un plato", description = "Elimina un plato de la carta por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plato eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlato(@PathVariable Long id) {
        log.warn("DELETE /api/platos/{} - Eliminando plato de la carta", id);
        platoService.eliminarPlato(id);
        log.info("Plato con ID {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }
}


