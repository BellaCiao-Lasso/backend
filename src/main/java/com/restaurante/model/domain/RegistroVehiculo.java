package com.restaurante.model.domain;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroVehiculo {
    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double cobro;

    public Double calcularCobro() {
        if (entrada != null && salida != null) {
            long horas = Duration.between(entrada, salida).toHours();
            return horas * 5000.0;
        }
        return 0.0;
    }
    public void registrarSalida() {
        this.salida = LocalDateTime.now();
        this.cobro = calcularCobro();
    }
    public Boolean estaActivo() {
        return this.salida == null;
    }
}
