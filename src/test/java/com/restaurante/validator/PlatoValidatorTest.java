package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.repository.PlatoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatoValidatorTest {

    @Mock
    private PlatoRepository platoRepository;

    @InjectMocks
    private PlatoValidator platoValidator;

    @Test
    @DisplayName("✅ Validar nombre único cuando no existe en BD - no lanza excepción")
    void validarNombreUnico_cuandoNoExiste_noLanzaExcepcion() {
        // Arrange
        when(platoRepository.existsByNombreIgnoreCase("Risotto")).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> platoValidator.validarNombreUnico("Risotto"));
    }

    @Test
    @DisplayName("❌ Validar nombre único cuando ya existe en BD - lanza PlatoAlreadyExistsException (409)")
    void validarNombreUnico_cuandoYaExiste_lanzaPlatoAlreadyExistsException() {
        // Arrange
        when(platoRepository.existsByNombreIgnoreCase("Risotto")).thenReturn(true);

        // Act & Assert
        assertThrows(PlatoAlreadyExistsException.class,
                () -> platoValidator.validarNombreUnico("Risotto"));
    }

    @Test
    @DisplayName("❌ Validar nombre único al actualizar otro plato con el mismo nombre - lanza PlatoAlreadyExistsException (409)")
    void validarNombreUnicoParaActualizar_cuandoDuplicadoEnOtroId_lanzaExcepcion() {
        // Arrange
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Risotto", 5L)).thenReturn(true);

        // Act & Assert
        assertThrows(PlatoAlreadyExistsException.class,
                () -> platoValidator.validarNombreUnicoParaActualizar("Risotto", 5L));
    }

    @Test
    @DisplayName("❌ Validar datos básicos de plato inválido - lanza ReglaDeNegocioException (422)")
    void validarDatosBasicos_cuandoPrecioNegativo_lanzaReglaDeNegocioException() {
        // Arrange
        Plato platoInvalido = new Plato(null, "Plato", -100.0, "ENTRADA", true);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class,
                () -> platoValidator.validarDatosBasicos(platoInvalido));
    }

    @Test
    @DisplayName("✅ Patrón State en Pedido - flujo válido RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO")
    void pedidoState_flujoValidoCompleto() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(1L)
                .idMesa(1L)
                .estado(EstadoPedido.RECIBIDO)
                .build();

        // Act & Assert
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());

        pedido.cambiarEstado(EstadoPedido.LISTO);
        assertEquals(EstadoPedido.LISTO, pedido.getEstado());

        pedido.cambiarEstado(EstadoPedido.ENTREGADO);
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    @DisplayName("❌ Patrón State Deny-by-Default en Pedido - transición inválida desde ENTREGADO lanza EstadoInvalidoException (422)")
    void pedidoState_transicionInvalidaDesdeEntregado_lanzaEstadoInvalidoException() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(1L)
                .idMesa(1L)
                .estado(EstadoPedido.ENTREGADO)
                .build();

        // Act & Assert
        assertThrows(EstadoInvalidoException.class,
                () -> pedido.cambiarEstado(EstadoPedido.CANCELADO));
        assertThrows(EstadoInvalidoException.class,
                () -> pedido.cambiarEstado(EstadoPedido.EN_PREPARACION));
        assertThrows(PedidoNoModificableException.class,
                () -> pedido.agregarItem(ItemPedido.builder().idPlato(1L).cantidad(1).build()));
    }
}
