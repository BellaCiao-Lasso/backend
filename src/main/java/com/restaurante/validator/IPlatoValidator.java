package com.restaurante.validator;

import com.restaurante.model.domain.Plato;

public interface IPlatoValidator {
    void validarNombreUnico(String nombre);
    void validarNombreUnicoParaActualizar(String nombre, Long id);
    void validarDatosBasicos(Plato plato);
}

