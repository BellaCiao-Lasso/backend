package com.restaurante.validator.impl;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.repository.MesaRepository;
import com.restaurante.validator.IMesaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MesaValidatorImpl implements IMesaValidator {

    private final MesaRepository mesaRepository;

    @Override
    public void validarNumeroUnico(Integer numero) {
        if (mesaRepository.existsByNumero(numero)) {
            throw new MesaAlreadyExistsException("Ya existe una mesa con el número " + numero);
        }
    }

    @Override
    public void validarCambioEstado(Mesa mesa, EstadoMesa nuevoEstado) {
        if (nuevoEstado == EstadoMesa.DISPONIBLE && Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("No se puede marcar la mesa como disponible si la cuenta sigue abierta.");
        }
    }

    @Override
    public void validarAperturaCuenta(Mesa mesa) {
        if (Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("La cuenta de la mesa ya está abierta.");
        }
    }

    @Override
    public void validarCierreCuenta(Mesa mesa) {
        if (!Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("La cuenta de la mesa no está abierta.");
        }
    }
}
