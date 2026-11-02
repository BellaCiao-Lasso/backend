package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.persistence.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PedidoServiceImpl implements PedidoService {

    private final PlatoService platoService;
    private final MesaService mesaService;

    private final PedidoRepository pedidoRepository;
    private final PedidoEntityMapper entityMapper;
    private final PedidoMapperIn mapperIn;
    private final PedidoMapperOut mapperOut;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        log.info("Iniciando creación de pedido para la mesa {}", requestDTO.getIdMesa());

        // 1. Validar mesa directamente por ID (sin cargar toda la tabla en memoria)
        MesaResponseDTO mesa;
        try {
            mesa = mesaService.obtenerPorId(requestDTO.getIdMesa());
        } catch (MesaNotFoundException ex) {
            throw new EstadoInvalidoException("La mesa con ID " + requestDTO.getIdMesa() + " no existe.");
        }
        if (mesa == null) {
            throw new EstadoInvalidoException("La mesa con ID " + requestDTO.getIdMesa() + " no existe.");
        }

        // 2. Regla de negocio: La mesa debe estar ocupada (con cuenta abierta)
        if (!"OCUPADA".equals(mesa.getEstado())) {
            log.warn("Intento de pedido en mesa no ocupada (ID: {})", requestDTO.getIdMesa());
            throw new EstadoInvalidoException("La mesa debe estar OCUPADA para recibir pedidos.");
        }

        Pedido pedido = mapperIn.toDomain(requestDTO);
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setTimestamp(LocalDateTime.now());

        // 3. Agregar items validando disponibilidad
        for (ItemPedidoRequestDTO itemDto : requestDTO.getItems()) {
            if (!platoService.existeYEstaDisponible(itemDto.getIdPlato())) {
                log.error("El plato {} no está disponible o no existe", itemDto.getIdPlato());
                throw new EstadoInvalidoException("El plato con ID " + itemDto.getIdPlato() + " no existe o no está disponible.");
            }

            PlatoResponseDTO plato = platoService.obtenerPorId(itemDto.getIdPlato());

            ItemPedido item = ItemPedido.builder()
                    .idPlato(itemDto.getIdPlato())
                    .nombrePlato(plato.getNombre())
                    .precioCongelado(plato.getPrecio())
                    .cantidad(itemDto.getCantidad())
                    .build();

            pedido.agregarItem(item);
        }

        PedidoEntity entity = entityMapper.toEntity(pedido);

        final PedidoEntity finalEntity = entity;
        if (finalEntity.getItems() != null) {
            finalEntity.getItems().forEach(i -> i.setPedido(finalEntity));
        }

        entity = pedidoRepository.save(finalEntity);
        log.info("Pedido {} creado exitosamente en BD con {} items", entity.getId(), entity.getItems().size());

        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    @Override
    public List<PedidoResponseDTO> obtenerPorMesa(Long idMesa) {
        log.info("Consultando pedidos de la mesa {}", idMesa);
        return pedidoRepository.findByIdMesa(idMesa).stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PedidoResponseDTO actualizarEstado(Long idPedido, String nuevoEstado) {
        PedidoEntity entity = buscarOLanzar(idPedido);
        Pedido pedido = entityMapper.toDomain(entity);

        EstadoPedido estado;
        try {
            estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("El estado '" + nuevoEstado + "' no es válido.");
        }

        pedido.cambiarEstado(estado);
        PedidoEntity actualizada = entityMapper.toEntity(pedido);
        if (actualizada.getItems() != null) {
            actualizada.getItems().forEach(i -> i.setPedido(actualizada));
        }
        entity = pedidoRepository.save(actualizada);

        log.info("Pedido {} cambió a estado {}", idPedido, estado);
        return mapperOut.toDTO(entityMapper.toDomain(entity));
    }

    private PedidoEntity buscarOLanzar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado con id: " + id));
    }
}
