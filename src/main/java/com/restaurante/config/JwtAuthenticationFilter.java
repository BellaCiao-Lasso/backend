package com.restaurante.config;

import com.restaurante.service.security.JwtService;
import com.restaurante.service.security.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        // 1. Si no hay header o no es un token Bearer, ignora este filtro y sigue la cadena
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token y el email del Payload
        jwt = authHeader.substring(7); // "Bearer " son 7 caracteres
        userEmail = jwtService.extraerUsername(jwt);

        // 3. Si se obtuvo el email y el usuario AÚN NO está autenticado en este hilo
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Cargar datos de base de datos
            UserDetails userDetails = this.usuarioDetailsService.loadUserByUsername(userEmail);

            // 4. Validar que el token sea correcto
            if (jwtService.validarToken(jwt, userDetails)) {
                
                // 5. Crear el objeto de autenticación manual
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 6. Inyectarlo en el Contexto de Seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 7. Continuar al controlador
        filterChain.doFilter(request, response);
    }
}
