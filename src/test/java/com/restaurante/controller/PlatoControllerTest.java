package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class PlatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Paso 6 Diapositiva: Ciclo completo (CRÉALA, LÍSTALA, ACTUALÍZALE UN DATO, BÓRRALA)")
    void cicloCompleto_entidadPlato() throws Exception {
        // Arrange: 1. CRÉALA (POST /api/v1/platos)
        PlatoRequestDTO nuevoPlato = new PlatoRequestDTO(
                "Ravioli de Espinaca",
                29000.0,
                "PLATO_FUERTE",
                "Pasta rellena de ricota y espinaca con salsa blanca",
                true
        );

        // Act & Assert: Crear
        MvcResult resultadoPost = mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoPlato)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Ravioli de Espinaca"))
                .andExpect(jsonPath("$.disponible").value(true))
                .andReturn();

        String respuestaJson = resultadoPost.getResponse().getContentAsString();
        Long idGenerado = objectMapper.readTree(respuestaJson).get("id").asLong();

        // Act & Assert: 2. LÍSTALA (GET /api/v1/platos y GET /api/v1/menu)
        mockMvc.perform(get("/api/v1/platos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Act & Assert: 2.1 Buscar por nombre (@Query JPQL de la Diapositiva 1)
        mockMvc.perform(get("/api/v1/platos/nombre/{nombre}", "Ravioli de Espinaca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idGenerado))
                .andExpect(jsonPath("$.nombre").value("Ravioli de Espinaca"));

        // Act & Assert: 3. ACTUALÍZALE UN DATO (PATCH /api/v1/platos/{id}/disponibilidad a false)
        mockMvc.perform(patch("/api/v1/platos/{id}/disponibilidad", idGenerado)
                        .param("disponible", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));

        // Arrange: 3.1 ACTUALÍZALE UN DATO (PUT /api/v1/platos/{id})
        PlatoRequestDTO platoActualizado = new PlatoRequestDTO(
                "Ravioli de Espinaca y Ricota",
                31000.0,
                "PLATO_FUERTE",
                "Nueva receta con salsa de nueces",
                true
        );

        // Act & Assert: Actualizar completo
        mockMvc.perform(put("/api/v1/platos/{id}", idGenerado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(platoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ravioli de Espinaca y Ricota"))
                .andExpect(jsonPath("$.precio").value(31000.0));

        // Act & Assert: 4. BÓRRALA (DELETE /api/v1/platos/{id})
        mockMvc.perform(delete("/api/v1/platos/{id}", idGenerado))
                .andExpect(status().isNoContent());

        // Assert: Verificar que ya no existe (404)
        mockMvc.perform(get("/api/v1/platos/{id}", idGenerado))
                .andExpect(status().isNotFound());
    }
}
