package com.restaurante.controller;

import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final PlatoService platoService;

    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> obtenerMenuCompleto() {
        // Obtenemos solo los que están disponibles, no todos
        List<PlatoResponseDTO> disponibles = platoService.obtenerDisponibles();
        return ResponseEntity.ok(disponibles);
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PlatoResponseDTO>> obtenerMenuPorCategoria(@PathVariable String categoria) {
        List<PlatoResponseDTO> filtrados = platoService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(filtrados);
    }
}
