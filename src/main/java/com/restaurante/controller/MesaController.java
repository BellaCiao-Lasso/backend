package com.restaurante.controller;

import com.restaurante.controller.docs.MesaApi;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
public class MesaController implements MesaApi {

    private final MesaService mesaService;

    @Override
    @PostMapping
    public ResponseEntity<MesaResponseDTO> crearMesa(@Valid @RequestBody MesaRequestDTO requestDTO) {
        log.info("POST /api/v1/mesas - Solicitud para crear mesa número {}", requestDTO.getNumero());
        MesaResponseDTO nuevaMesa = mesaService.crearMesa(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMesa);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/mesas - Consultando todas las mesas");
        return ResponseEntity.ok(mesaService.obtenerTodas());
    }

    @Override
    @GetMapping("/disponibles")
    public ResponseEntity<List<MesaResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/v1/mesas/disponibles - Consultando mesas disponibles");
        return ResponseEntity.ok(mesaService.obtenerDisponibles());
    }

    @Override
    @PostMapping("/{id}/abrir-cuenta")
    public ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id) {
        log.info("POST /api/v1/mesas/{}/abrir-cuenta - Abriendo cuenta", id);
        return ResponseEntity.ok(mesaService.abrirCuenta(id));
    }

    @Override
    @PostMapping("/{id}/cerrar-cuenta")
    public ResponseEntity<MesaResponseDTO> cerrarCuenta(@PathVariable Long id) {
        log.info("POST /api/v1/mesas/{}/cerrar-cuenta - Cerrando cuenta", id);
        return ResponseEntity.ok(mesaService.cerrarCuenta(id));
    }
}
