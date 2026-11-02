package com.restaurante.controller.docs;

import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
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

@Tag(name = "Platos", description = "Gestión de la carta del restaurante")
public interface PlatoApi {

    @Operation(summary = "Obtener todos los platos", description = "Devuelve la carta completa con estado de disponibilidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carta obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<List<PlatoResponseDTO>> listarPlatos();

    @Operation(summary = "Obtener un plato por ID", description = "Devuelve un plato específico buscándolo por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    ResponseEntity<PlatoResponseDTO> obtenerPlato(@PathVariable Long id);

    @Operation(summary = "Buscar plato por nombre", description = "Busca un plato por su nombre usando consulta JPQL personalizada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el nombre proporcionado")
    })
    ResponseEntity<PlatoResponseDTO> buscarPorNombre(@PathVariable String nombre);

    @Operation(summary = "Crear un plato", description = "Agrega un nuevo plato a la carta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un plato con ese nombre")
    })
    ResponseEntity<PlatoResponseDTO> crearPlato(@RequestBody @Valid PlatoRequestDTO platoRequestDTO);

    @Operation(summary = "Actualizar un plato", description = "Modifica los datos de un plato existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    ResponseEntity<PlatoResponseDTO> actualizarPlato(@PathVariable Long id, @RequestBody @Valid PlatoRequestDTO platoRequestDTO);

    @Operation(summary = "Actualizar disponibilidad de un plato", description = "Modifica únicamente la disponibilidad (true/false) de un plato en el menú")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado")
    })
    ResponseEntity<PlatoResponseDTO> actualizarDisponibilidad(@PathVariable Long id, @RequestParam Boolean disponible);

    @Operation(summary = "Eliminar un plato", description = "Elimina un plato de la carta por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plato eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado con el ID proporcionado")
    })
    ResponseEntity<Void> eliminarPlato(@PathVariable Long id);
}
