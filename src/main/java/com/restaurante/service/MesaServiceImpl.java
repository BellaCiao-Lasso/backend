package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.mapper.MesaMapperIn;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final MesaEntityMapper entityMapper;
    private final MesaMapperIn mapperIn;
    private final MesaMapperOut mapperOut;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesaResponseDTO crearMesa(MesaRequestDTO requestDTO) {
        log.info("Intentando crear mesa número {}", requestDTO.getNumero());

        if (mesaRepository.existsByNumero(requestDTO.getNumero())) {
            log.warn("El número de mesa {} ya existe", requestDTO.getNumero());
            throw new MesaAlreadyExistsException("Ya existe una mesa con el número " + requestDTO.getNumero());
        }

        Mesa mesa = mapperIn.toDomain(requestDTO);
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setCuentaAbierta(false);

        MesaEntity entity = entityMapper.toEntity(mesa);
        entity = mesaRepository.save(entity);

        log.info("Mesa {} creada exitosamente con ID {}", entity.getNumero(), entity.getId());

        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    @Override
    public MesaResponseDTO obtenerPorId(Long id) {
        log.debug("Consultando mesa con ID {}", id);
        MesaEntity entity = buscarOLanzar(id);
        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    @Override
    public List<MesaResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las mesas");
        return mesaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public List<MesaResponseDTO> obtenerDisponibles() {
        log.info("Buscando mesas disponibles...");
        return mesaRepository.findByEstado(EstadoMesa.DISPONIBLE.name()).stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesaResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        MesaEntity entity = buscarOLanzar(id);
        Mesa mesa = entityMapper.toDomain(entity);

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
        entity = mesaRepository.save(entityMapper.toEntity(mesa));

        log.info("Mesa {} (ID: {}) cambió a estado {}", mesa.getNumero(), id, estado);
        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesaResponseDTO abrirCuenta(Long id) {
        MesaEntity entity = buscarOLanzar(id);
        Mesa mesa = entityMapper.toDomain(entity);

        if (Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("La cuenta de la mesa ya está abierta.");
        }
        mesa.abrirCuenta();

        entity = mesaRepository.save(entityMapper.toEntity(mesa));
        log.info("Cuenta abierta para mesa {} (ID: {})", mesa.getNumero(), id);
        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesaResponseDTO cerrarCuenta(Long id) {
        MesaEntity entity = buscarOLanzar(id);
        Mesa mesa = entityMapper.toDomain(entity);

        if (!Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new EstadoInvalidoException("La cuenta de la mesa no está abierta.");
        }
        mesa.cerrarCuenta();

        entity = mesaRepository.save(entityMapper.toEntity(mesa));
        log.info("Cuenta cerrada para mesa {} (ID: {})", mesa.getNumero(), id);
        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    private MesaEntity buscarOLanzar(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + id));
    }
}
