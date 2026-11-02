package com.restaurante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("❌ Crear plato sin token JWT devuelve 401 Unauthorized")
    void crearPlato_sinToken_devuelve401() throws Exception {
        // Arrange
        String payload = "{\"nombre\":\"Bandeja\",\"precio\":28000,\"categoria\":\"PLATO_FUERTE\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("❌ Crear plato con rol CLIENTE devuelve 403 Forbidden")
    void crearPlato_conRolCliente_devuelve403() throws Exception {
        // Arrange
        String payload = "{\"nombre\":\"Bandeja\",\"precio\":28000,\"categoria\":\"PLATO_FUERTE\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("✅ Crear plato con rol ADMIN devuelve 201 Created")
    void crearPlato_conRolAdmin_devuelve201() throws Exception {
        // Arrange
        String payload = "{\"nombre\":\"Plato Seguridad " + System.currentTimeMillis() + "\",\"precio\":35000,\"categoria\":\"PLATO_FUERTE\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }
}
