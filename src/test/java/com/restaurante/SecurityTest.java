package com.restaurante;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearPlato_sinToken_devuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bandeja\",\"precio\":28000,\"categoria\":\"PLATO_FUERTE\"}"))
                .andExpect(status().isUnauthorized()); // Esperamos 401 Unauthorized
    }

    @Test
    @WithMockUser(roles = "CLIENTE") // Simulamos ser un CLIENTE (No tiene permisos para crear platos)
    void crearPlato_conRolCliente_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bandeja\",\"precio\":28000,\"categoria\":\"PLATO_FUERTE\"}"))
                .andExpect(status().isForbidden()); // Esperamos 403 Forbidden
    }
    
    @Test
    @WithMockUser(roles = "ADMIN") // Simulamos ser un ADMIN (Sí tiene permisos para crear platos)
    void crearPlato_conRolAdmin_devuelve201() throws Exception {
        // Asumiendo que crear un plato devuelve 200 OK o 201 Created cuando es exitoso
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Plato Seguridad " + System.currentTimeMillis() + "\",\"precio\":35000,\"categoria\":\"PLATO_FUERTE\"}"))
                .andExpect(status().isCreated()); // Asumiendo 201 Created (o cambia a isOk() según tu controller)
    }
}
