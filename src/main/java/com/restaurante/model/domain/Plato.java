package com.restaurante.model.domain;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {
    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private String descripcion;
    private Boolean disponible;

    public Plato(Long id, String nombre, Double precio, String categoria, Boolean disponible) {
        this(id, nombre, precio, categoria, null, disponible);
    }

    public Boolean esValido() {
        return nombre != null && !nombre.isEmpty() && precio != null && precio > 0;
    }
}
