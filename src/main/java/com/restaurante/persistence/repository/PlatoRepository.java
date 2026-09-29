package com.restaurante.persistence.repository;

import com.restaurante.persistence.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatoRepository extends JpaRepository<PlatoEntity, Long> {

    // 1. Consultas derivadas por nombre de método (Diapositiva 1)
    List<PlatoEntity> findByDisponibleTrue();
    List<PlatoEntity> findByCategoriaIgnoreCase(String categoria);
    List<PlatoEntity> findByCategoriaAndDisponible(String categoria, Boolean disponible);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    // 2. Consultas manuales con @Query usando JPQL y @Param (Diapositiva 1)
    @Query("SELECT p FROM PlatoEntity p WHERE LOWER(p.nombre) = LOWER(:nombre)")
    Optional<PlatoEntity> buscarPorNombre(@Param("nombre") String nombre);
}
