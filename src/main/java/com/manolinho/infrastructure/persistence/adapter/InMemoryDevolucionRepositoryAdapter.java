package com.manolinho.infrastructure.persistence.adapter;

import com.manolinho.domain.model.Devolucion;
import com.manolinho.domain.repository.DevolucionRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDevolucionRepositoryAdapter implements DevolucionRepository {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, Devolucion> storage = new ConcurrentHashMap<>();

    @Override
    public Devolucion save(Devolucion devolucion) {
        Long id = devolucion.getId() != null ? devolucion.getId() : sequence.incrementAndGet();
        Devolucion persisted = new Devolucion(
                id,
                devolucion.getPedidoId(),
                devolucion.getFechaSolicitud(),
                devolucion.getLineasDevueltas(),
                devolucion.getImporteReembolso(),
                devolucion.getEstado()
        );
        storage.put(id, persisted);
        return persisted;
    }
}
