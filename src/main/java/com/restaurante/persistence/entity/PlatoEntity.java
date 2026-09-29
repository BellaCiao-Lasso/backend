package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// persistence/entity/PlatoEntity.java
// Esta clase representa la tabla "platos" en PostgreSQL
// No tiene lógica de negocio - solo estructura y anotaciones JPA
@Entity
@Table(name = "platos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 60)
    private String categoria;

    private String descripcion;

    @Column(nullable = false)
    private Boolean disponible;

    @CreationTimestamp
    private LocalDateTime creadoEn;
}
