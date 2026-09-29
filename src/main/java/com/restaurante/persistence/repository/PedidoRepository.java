package com.restaurante.persistence.repository;

import com.restaurante.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    // Solución 1 al problema N+1 con EntityGraph (Diapositiva 4)
    @EntityGraph(attributePaths = {"items"})
    List<PedidoEntity> findByIdMesa(Long idMesa);

    // Solución 2 al problema N+1 con JOIN FETCH (Diapositiva 4)
    @Query("SELECT DISTINCT p FROM PedidoEntity p LEFT JOIN FETCH p.items WHERE p.idMesa = :idMesa")
    List<PedidoEntity> findByIdMesaWithItems(@Param("idMesa") Long idMesa);
}
