package com.restaurante.controller.docs;

import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroRequestDTO;
import com.restaurante.model.dto.response.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticación", description = "Registro y Login de usuarios (JWT)")
public interface AuthApi {

    @Operation(summary = "Registrar nuevo usuario", description = "Guarda un usuario en la BD encriptando su contraseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    ResponseEntity<String> registrarUsuario(@Valid @RequestBody RegistroRequestDTO request);

    @Operation(summary = "Iniciar sesión", description = "Recibe email y password. Devuelve el JWT Token si son correctos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request);
}

