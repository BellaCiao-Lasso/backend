package com.restaurante.controller;

import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
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
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas v1", description = "Gestión de las mesas del restaurante")
public class MesaController {

    private final MesaService mesaService;

    @Operation(summary = "Crear nueva mesa", description = "Registra una nueva mesa con número único en el restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mesa creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej. número menor a 1)"),
            @ApiResponse(responseCode = "409", description = "El número de mesa ya existe")
    })
    @PostMapping
    public ResponseEntity<MesaResponseDTO> crearMesa(@Valid @RequestBody MesaRequestDTO requestDTO) {
        log.info("POST /api/v1/mesas - Solicitud para crear mesa número {}", requestDTO.getNumero());
        MesaResponseDTO nuevaMesa = mesaService.crearMesa(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMesa);
    }

    @Operation(summary = "Listar todas las mesas", description = "Obtiene todas las mesas registradas con sus estados")
    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/mesas - Consultando todas las mesas");
        return ResponseEntity.ok(mesaService.obtenerTodas());
    }

    @Operation(summary = "Listar mesas disponibles", description = "Obtiene únicamente las mesas con estado DISPONIBLE")
    @GetMapping("/disponibles")
    public ResponseEntity<List<MesaResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/v1/mesas/disponibles - Consultando mesas disponibles");
        return ResponseEntity.ok(mesaService.obtenerDisponibles());
    }

    @Operation(summary = "Abrir cuenta de una mesa", description = "Marca la cuenta como abierta y el estado pasa a OCUPADA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta abierta correctamente"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
            @ApiResponse(responseCode = "422", description = "La cuenta ya estaba abierta")
    })
    @PostMapping("/{id}/abrir-cuenta")
    public ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id) {
        log.info("POST /api/v1/mesas/{}/abrir-cuenta - Abriendo cuenta", id);
        return ResponseEntity.ok(mesaService.abrirCuenta(id));
    }

    @Operation(summary = "Cerrar cuenta de una mesa", description = "Cierra la cuenta y marca la mesa como DISPONIBLE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta cerrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
            @ApiResponse(responseCode = "422", description = "La cuenta no estaba abierta")
    })
    @PostMapping("/{id}/cerrar-cuenta")
    public ResponseEntity<MesaResponseDTO> cerrarCuenta(@PathVariable Long id) {
        log.info("POST /api/v1/mesas/{}/cerrar-cuenta - Cerrando cuenta", id);
        return ResponseEntity.ok(mesaService.cerrarCuenta(id));
    }
}
