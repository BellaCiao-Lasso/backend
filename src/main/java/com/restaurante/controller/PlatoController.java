package com.restaurante.controller;

import com.restaurante.controller.docs.PlatoApi;
import com.restaurante.mapper.PlatoMapperIn;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoController implements PlatoApi {

    private final PlatoService platoService;
    private final PlatoMapperIn mapperIn;

    @Override
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> listarPlatos() {
        log.info("GET /api/v1/platos - Solicitando lista completa de platos");
        List<PlatoResponseDTO> platos = platoService.obtenerTodos();
        log.info("Se encontraron {} platos en la carta", platos.size());
        return ResponseEntity.ok(platos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> obtenerPlato(@PathVariable Long id) {
        log.info("GET /api/v1/platos/{} - Buscando plato por ID", id);
        return ResponseEntity.ok(platoService.obtenerPorId(id));
    }

    @Override
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<PlatoResponseDTO> buscarPorNombre(@PathVariable String nombre) {
        log.info("GET /api/v1/platos/nombre/{} - Buscando plato por nombre", nombre);
        return ResponseEntity.ok(platoService.buscarPorNombre(nombre));
    }

    @Override
    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crearPlato(@RequestBody @Valid PlatoRequestDTO platoRequestDTO) {
        log.info("POST /api/v1/platos - Creando plato: {}", platoRequestDTO.nombre());
        Plato platoDominio = mapperIn.toDomain(platoRequestDTO);
        PlatoResponseDTO respuesta = platoService.crearPlato(platoDominio);
        log.info("Plato creado exitosamente con ID: {}", respuesta.getId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> actualizarPlato(@PathVariable Long id, @RequestBody @Valid PlatoRequestDTO platoRequestDTO) {
        log.info("PUT /api/v1/platos/{} - Actualizando plato", id);
        Plato platoDominio = mapperIn.toDomain(platoRequestDTO);
        PlatoResponseDTO respuesta = platoService.actualizarPlato(id, platoDominio);
        log.info("Plato con ID {} actualizado correctamente", id);
        return ResponseEntity.ok(respuesta);
    }

    @Override
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<PlatoResponseDTO> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        log.info("PATCH /api/v1/platos/{}/disponibilidad - Cambiando disponibilidad a {}", id, disponible);
        PlatoResponseDTO respuesta = platoService.actualizarDisponibilidad(id, disponible);
        return ResponseEntity.ok(respuesta);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlato(@PathVariable Long id) {
        log.warn("DELETE /api/v1/platos/{} - Eliminando plato de la carta", id);
        platoService.eliminarPlato(id);
        log.info("Plato con ID {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }
}
