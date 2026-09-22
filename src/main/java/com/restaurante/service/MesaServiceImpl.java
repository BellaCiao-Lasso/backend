package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaMapperIn;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaMapperIn mapperIn;
    private final MesaMapperOut mapperOut;

    private final Map<Long, Mesa> mesas = new ConcurrentHashMap<>();
    private final AtomicLong secuencias = new AtomicLong(1);

    @Override
    public MesaResponseDTO crearMesa(MesaRequestDTO requestDTO) {
        log.info("Intentando crear mesa número {}", requestDTO.getNumero());

        boolean existeNumero = mesas.values().stream()
                .anyMatch(m -> m.getNumero().equals(requestDTO.getNumero()));

        if (existeNumero) {
            log.warn("El número de mesa {} ya existe", requestDTO.getNumero());
            throw new MesaAlreadyExistsException("Ya existe una mesa con el número " + requestDTO.getNumero());
        }

        Mesa mesa = mapperIn.toDomain(requestDTO);
        mesa.setId(secuencias.getAndIncrement());
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setCuentaAbierta(false);

        mesas.put(mesa.getId(), mesa);
        log.info("Mesa {} creada exitosamente con ID {}", mesa.getNumero(), mesa.getId());

        return mapperOut.toDTO(mesa);
    }

    @Override
    public List<MesaResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las mesas (Total: {})", mesas.size());
        return mesas.values().stream()
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<MesaResponseDTO> obtenerDisponibles() {
        log.info("Buscando mesas disponibles...");
        return mesas.values().stream()
                .filter(Mesa::estaDisponible)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public MesaResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        Mesa mesa = buscarOLanzar(id);
        
        EstadoMesa estado;
        try {
            estado = EstadoMesa.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("El estado '" + nuevoEstado + "' no es válido.");
        }

        if (estado == EstadoMesa.DISPONIBLE && Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("No se puede marcar la mesa como disponible si la cuenta sigue abierta.");
        }

        mesa.setEstado(estado);
        log.info("Mesa {} (ID: {}) cambió a estado {}", mesa.getNumero(), id, estado);
        return mapperOut.toDTO(mesa);
    }

    @Override
    public MesaResponseDTO abrirCuenta(Long id) {
        Mesa mesa = buscarOLanzar(id);
        if (Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("La cuenta de la mesa ya está abierta.");
        }
        mesa.abrirCuenta();
        log.info("Cuenta abierta para mesa {} (ID: {})", mesa.getNumero(), id);
        return mapperOut.toDTO(mesa);
    }

    @Override
    public MesaResponseDTO cerrarCuenta(Long id) {
        Mesa mesa = buscarOLanzar(id);
        if (!Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("La cuenta de la mesa no está abierta.");
        }
        mesa.cerrarCuenta();
        log.info("Cuenta cerrada para mesa {} (ID: {})", mesa.getNumero(), id);
        return mapperOut.toDTO(mesa);
    }

    private Mesa buscarOLanzar(Long id) {
        return Optional.ofNullable(mesas.get(id))
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + id));
    }
}
