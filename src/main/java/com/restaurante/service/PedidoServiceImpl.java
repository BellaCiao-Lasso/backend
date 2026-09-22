package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PlatoService platoService;
    private final MesaService mesaService;
    
    private final PedidoMapperIn mapperIn;
    private final PedidoMapperOut mapperOut;

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong secuencias = new AtomicLong(1);
    private final AtomicLong secuenciasItems = new AtomicLong(1);

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        log.info("Iniciando creación de pedido para la mesa {}", requestDTO.getIdMesa());

        // 1. Validar mesa (si no existe, MesaService lanza MesaNotFoundException)
        var mesas = mesaService.obtenerTodas();
        boolean mesaExiste = mesas.stream().anyMatch(m -> m.getId().equals(requestDTO.getIdMesa()));
        if (!mesaExiste) {
             throw new EstadoInvalidoException("La mesa con ID " + requestDTO.getIdMesa() + " no existe.");
        }
        
        var mesa = mesas.stream().filter(m -> m.getId().equals(requestDTO.getIdMesa())).findFirst().get();

        // 2. Regla de negocio: La mesa debe estar ocupada (con cuenta abierta)
        if (!"OCUPADA".equals(mesa.getEstado())) {
            log.warn("Intento de pedido en mesa no ocupada (ID: {})", requestDTO.getIdMesa());
            throw new EstadoInvalidoException("La mesa debe estar OCUPADA para recibir pedidos.");
        }

        Pedido pedido = mapperIn.toDomain(requestDTO);
        pedido.setId(secuencias.getAndIncrement());
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
                    .id(secuenciasItems.getAndIncrement())
                    .idPlato(itemDto.getIdPlato())
                    .nombrePlato(plato.getNombre())
                    .precioCongelado(plato.getPrecio())
                    .cantidad(itemDto.getCantidad())
                    .build();
            
            pedido.agregarItem(item);
        }

        pedidos.put(pedido.getId(), pedido);
        log.info("Pedido {} creado exitosamente con {} items", pedido.getId(), pedido.getItems().size());
        
        return mapperOut.toDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> obtenerPorMesa(Long idMesa) {
        log.info("Consultando pedidos de la mesa {}", idMesa);
        return pedidos.values().stream()
                .filter(p -> p.getIdMesa().equals(idMesa))
                .map(mapperOut::toDTO)
                .toList();
    }

    @Override
    public PedidoResponseDTO actualizarEstado(Long idPedido, String nuevoEstado) {
        Pedido pedido = buscarOLanzar(idPedido);
        
        EstadoPedido estado;
        try {
            estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("El estado '" + nuevoEstado + "' no es válido.");
        }

        pedido.cambiarEstado(estado);
        log.info("Pedido {} cambió a estado {}", idPedido, estado);
        return mapperOut.toDTO(pedido);
    }

    private Pedido buscarOLanzar(Long id) {
        return Optional.ofNullable(pedidos.get(id))
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado con id: " + id));
    }
}
