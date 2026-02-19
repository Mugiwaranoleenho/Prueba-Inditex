package com.manolinho.domain.repository;

import com.manolinho.domain.model.Pedido;
import java.util.Optional;

public interface PedidoRepository {

    Pedido save(Pedido pedido);

    Optional<Pedido> findById(Long id);
}
