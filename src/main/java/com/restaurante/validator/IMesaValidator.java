package com.restaurante.validator;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;

public interface IMesaValidator {
    void validarNumeroUnico(Integer numero);
    void validarCambioEstado(Mesa mesa, EstadoMesa nuevoEstado);
    void validarAperturaCuenta(Mesa mesa);
    void validarCierreCuenta(Mesa mesa);
}
