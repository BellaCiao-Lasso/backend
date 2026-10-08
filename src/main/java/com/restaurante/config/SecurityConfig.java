package com.restaurante.config;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.persistence.repository.UsuarioRepository;
import com.restaurante.service.security.JwtService;
import com.restaurante.service.security.UsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;
    private final org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/v1/menu/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/platos/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/api/v1/platos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/platos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/platos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/platos/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.POST, "/api/v1/mesas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/mesas/**").hasAnyRole("MESERO", "CAJERO", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/mesas/*/abrir-cuenta").hasAnyRole("MESERO", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/mesas/*/cerrar-cuenta").hasAnyRole("CAJERO", "ADMIN")
                
                .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasRole("MESERO")
                .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mesa/**").hasAnyRole("MESERO", "CAJERO", "CHEF", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/pedidos/*/estado").hasAnyRole("CHEF", "ADMIN")
                
                .requestMatchers("/api/v1/auth/**").permitAll()
                
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler())
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String nombre = oauthUser.getAttribute("name");
            
            log.info("Login OAuth2 exitoso de Google. Email: {}", email);
            
            // Buscar o crear usuario en BD
            UsuarioEntity usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
                UsuarioEntity nuevo = new UsuarioEntity();
                nuevo.setEmail(email);
                nuevo.setPassword("OAUTH2_USER"); // No usa password local
                nuevo.setRol("CLIENTE"); // Rol por defecto
                return usuarioRepository.save(nuevo);
            });

            // Generar JWT y enviarlo en respuesta o redirect
            String token = jwtService.generarToken(usuarioDetailsService.loadUserByUsername(usuario.getEmail()));
            log.info("Token generado para el usuario OAuth2: {}", token);
            
            // Simular el redirect que pide la guía, o enviar a Swagger con el token
            response.sendRedirect("/swagger-ui/index.html?token=" + token);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
