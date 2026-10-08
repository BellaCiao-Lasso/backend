package com.restaurante.service.security;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity usuario = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
                
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())     // ya viene hasheado de la BD
                .roles(usuario.getRol())             // Ej: "ADMIN", "CHEF", "CLIENTE"
                .build();
    }
}
