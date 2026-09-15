package com.restaurante.service;

import com.restaurante.model.domain.Pedido;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PlatoService platoService; // <- reutiliza otro Service via interfaz
    private final PedidoMapperIn mapperIn;
    private final PedidoMapperOut mapperOut;

    private final List<Pedido> pedidos = new ArrayList<>(); // en memoria por ahora

    @Override
    public void confirmarPedido(Long idPlato) {
        if (!platoService.existeYEstaDisponible(idPlato)) {
            log.warn("Intento de confirmar un plato no disponible o inexistente: {}", idPlato);
            throw new RuntimeException("El plato no existe o no está disponible");
        }
        log.info("Pedido confirmado para el plato ID: {}", idPlato);
    }
}
