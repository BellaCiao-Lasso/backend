package com.restaurante.persistence.repository;

import com.restaurante.persistence.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<MesaEntity, Long> {
    boolean existsByNumero(Integer numero);
    List<MesaEntity> findByEstado(String estado);
}
