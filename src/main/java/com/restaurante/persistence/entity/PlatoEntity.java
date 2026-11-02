package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// persistence/entity/PlatoEntity.java
// Esta clase representa la tabla "platos" en PostgreSQL
// No tiene lógica de negocio - solo estructura y anotaciones JPA
@Entity
@Table(name = "platos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
