package com.restaurante.service.security;

import com.restaurante.exception.ConflictoException;
import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroRequestDTO;
import com.restaurante.model.dto.response.AuthResponseDTO;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional(rollbackFor = Exception.class)
    public String registrarUsuario(RegistroRequestDTO request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Intento de registro con email duplicado: {}", request.email());
            throw new ConflictoException("Error: El email ya está registrado");
        }

        UsuarioEntity nuevoUsuario = UsuarioEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .build();

        usuarioRepository.save(nuevoUsuario);
        log.info("Usuario registrado exitosamente: {}", request.email());
        return "Usuario registrado exitosamente";
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generarToken(userDetails);
        return new AuthResponseDTO(token);
    }
}

