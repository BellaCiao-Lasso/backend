package com.restaurante.model.domain;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {
    private Long id;
    private Long idMesa;
    private Double total;
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;

    public Double calcularTotal() {
        return this.total;
    }
    public void registrarPago() {
        this.estado = EstadoCuenta.EN_PAGO;
    }
    public void cerrarCuenta() {
        this.estado = EstadoCuenta.CERRADA;
    }
}
