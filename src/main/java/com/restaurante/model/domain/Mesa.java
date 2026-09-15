package com.restaurante.model.domain;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {
    private Long id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private Boolean cuentaAbierta;

    public Boolean estaDisponible() {
        return this.estado == EstadoMesa.DISPONIBLE;
    }
    public void abrirCuenta() {
        this.cuentaAbierta = true;
        this.estado = EstadoMesa.OCUPADA;
    }
    public void cerrarCuenta() {
        this.cuentaAbierta = false;
        this.estado = EstadoMesa.DISPONIBLE;
    }
}
