package com.restaurante.controller;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.persistence.repository.UsuarioRepository;
import com.restaurante.service.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro y Login de usuarios (JWT)")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(summary = "Registrar nuevo usuario", description = "Guarda un usuario en la BD encriptando su contraseña")
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioEntity usuario) {
        
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @Operation(summary = "Iniciar sesión", description = "Recibe email y password. Devuelve el JWT Token si son correctos")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        // 1. Validar credenciales usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. Si es válido, obtener los datos del usuario logueado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Generar JWT Token
        String token = jwtService.generarToken(userDetails);

        // 4. Devolver Token en JSON
        return ResponseEntity.ok(Map.of("token", token));
    }

    // DTO simple para recibir credenciales
    public record LoginRequest(String email, String password) {}
}
