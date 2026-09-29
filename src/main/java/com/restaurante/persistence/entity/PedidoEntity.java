package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idMesa;

    @Column(nullable = false, length = 30)
    private String estado;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemPedidoEntity> items = new ArrayList<>();

    public void agregarItem(ItemPedidoEntity item) {
        items.add(item);
        item.setPedido(this);
    }

    public void removerItem(ItemPedidoEntity item) {
        items.remove(item);
        item.setPedido(null);
    }
}
