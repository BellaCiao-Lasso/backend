package com.restaurante.controller;

import com.restaurante.controller.docs.MenuApi;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController implements MenuApi {

    private final PlatoService platoService;

    @Override
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> obtenerMenuCompleto() {
        log.info("GET /api/v1/menu - Solicitando menú completo de platos disponibles");
        List<PlatoResponseDTO> disponibles = platoService.obtenerDisponibles();
        log.info("Menú disponible: {} platos encontrados", disponibles.size());
        return ResponseEntity.ok(disponibles);
    }

    @Override
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PlatoResponseDTO>> obtenerMenuPorCategoria(@PathVariable String categoria) {
        log.info("GET /api/v1/menu/categoria/{} - Filtrando menú por categoría", categoria);
        List<PlatoResponseDTO> filtrados = platoService.obtenerPorCategoria(categoria);
        log.info("Se encontraron {} platos en la categoría '{}'", filtrados.size(), categoria);
        return ResponseEntity.ok(filtrados);
    }
}
