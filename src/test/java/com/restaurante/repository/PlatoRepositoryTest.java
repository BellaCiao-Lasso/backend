package com.restaurante.repository;

import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.persistence.repository.PlatoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlatoRepositoryTest {

    @Autowired
    private PlatoRepository platoRepository;

    @Test
    @DisplayName("1. CRÉALA: Guardar plato en BD")
    void guardarPlato_debeAsignarId() {
        PlatoEntity plato = PlatoEntity.builder()
                .nombre("Pasta Carbonara")
                .precio(32000.0)
                .categoria("PLATO_FUERTE")
                .descripcion("Spaghetti con guanciale y pecorino")
                .disponible(true)
                .build();

        PlatoEntity guardado = platoRepository.save(plato);

        assertNotNull(guardado.getId());
        assertEquals("Pasta Carbonara", guardado.getNombre());
        assertNotNull(guardado.getCreadoEn());
    }

    @Test
    @DisplayName("2. LÍSTALA: Consultar platos y probar consultas derivadas")
    void listarPlatos_debeRetornarPlatosDisponibles() {
        PlatoEntity plato = PlatoEntity.builder()
                .nombre("Bruschetta")
                .precio(18000.0)
                .categoria("ENTRADA")
                .disponible(true)
                .build();
        platoRepository.save(plato);

        List<PlatoEntity> disponibles = platoRepository.findByDisponibleTrue();
        assertFalse(disponibles.isEmpty());

        List<PlatoEntity> entradas = platoRepository.findByCategoriaIgnoreCase("ENTRADA");
        assertEquals(1, entradas.size());
    }

    @Test
    @DisplayName("2.1 Probar @Query JPQL con @Param buscarPorNombre (Diapositiva 1)")
    void buscarPorNombre_consultaJPQL() {
        PlatoEntity plato = PlatoEntity.builder()
                .nombre("Tiramisú")
                .precio(15000.0)
                .categoria("POSTRE")
                .disponible(true)
                .build();
        platoRepository.save(plato);

        Optional<PlatoEntity> encontrado = platoRepository.buscarPorNombre("tiramisú");
        assertTrue(encontrado.isPresent());
        assertEquals("Tiramisú", encontrado.get().getNombre());
    }

    @Test
    @DisplayName("3. ACTUALÍZALE UN DATO: Modificar disponibilidad y precio")
    void actualizarPlato_debePersistirCambio() {
        PlatoEntity plato = PlatoEntity.builder()
                .nombre("Lasagna")
                .precio(28000.0)
                .categoria("PLATO_FUERTE")
                .disponible(true)
                .build();
        PlatoEntity guardado = platoRepository.save(plato);

        guardado.setDisponible(false);
        guardado.setPrecio(30000.0);
        platoRepository.save(guardado);

        PlatoEntity actualizado = platoRepository.findById(guardado.getId()).orElseThrow();
        assertFalse(actualizado.getDisponible());
        assertEquals(30000.0, actualizado.getPrecio());
    }

    @Test
    @DisplayName("4. BÓRRALA: Eliminar plato de la BD")
    void eliminarPlato_debeRemoverDeBD() {
        PlatoEntity plato = PlatoEntity.builder()
                .nombre("Gelato")
                .precio(12000.0)
                .categoria("POSTRE")
                .disponible(true)
                .build();
        PlatoEntity guardado = platoRepository.save(plato);

        platoRepository.delete(guardado);

        Optional<PlatoEntity> eliminado = platoRepository.findById(guardado.getId());
        assertTrue(eliminado.isEmpty());
    }
}
