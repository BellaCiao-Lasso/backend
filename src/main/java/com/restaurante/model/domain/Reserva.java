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
public class Reserva {
    private Long id;
    private Long idMesa;
    private String cliente;
    private LocalDateTime fechaHora;
    private Integer comensales;

    public Boolean estaVigente() {
        return fechaHora != null && fechaHora.isAfter(LocalDateTime.now());
    }
    public void cancelar() {
    }
    public void reprogramar(LocalDateTime nuevaFechaHora) {
        this.fechaHora = nuevaFechaHora;
    }
}
