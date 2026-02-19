package com.manolinho.infrastructure.persistence.adapter;

import com.manolinho.domain.model.Pedido;
import com.manolinho.domain.repository.PedidoRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryPedidoRepositoryAdapter implements PedidoRepository {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, Pedido> storage = new ConcurrentHashMap<>();

    @Override
    public Pedido save(Pedido pedido) {
        Long id = pedido.getId() != null ? pedido.getId() : sequence.incrementAndGet();

        Pedido persisted = new Pedido(
                id,
                pedido.getNumeroPedido(),
                pedido.getFechaCompra(),
                pedido.getLineas(),
                pedido.getTotal(),
                pedido.getEstado()
        );

        storage.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
}
