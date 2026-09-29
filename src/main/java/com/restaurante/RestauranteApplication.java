package com.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableJpaRepositories(basePackages = "com.restaurante.persistence.repository") // Opcional: Spring Boot lo detecta automáticamente al estar en subpaquetes
public class RestauranteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestauranteApplication.class, args);
    }
}
