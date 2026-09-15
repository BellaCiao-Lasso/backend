package com.restaurante.controller;


import com.restaurante.mapper.PlatoMapperIn;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;
    private final PlatoMapperIn mapperIn; // El controller se encarga de transformar RequestDTO a Domain

    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> listarPlatos() {
        return ResponseEntity.ok(platoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> obtenerPlato(@PathVariable Long id) {
        return ResponseEntity.ok(platoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crearPlato(@RequestBody PlatoRequestDTO platoRequestDTO) {
        Plato platoDominio = mapperIn.toDomain(platoRequestDTO);
        return new ResponseEntity<>(platoService.crearPlato(platoDominio), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> actualizarPlato(@PathVariable Long id, @RequestBody PlatoRequestDTO platoRequestDTO) {
        Plato platoDominio = mapperIn.toDomain(platoRequestDTO);
        return ResponseEntity.ok(platoService.actualizarPlato(id, platoDominio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlato(@PathVariable Long id) {
        platoService.eliminarPlato(id);
        return ResponseEntity.noContent().build();
    }
}
