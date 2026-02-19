package com.manolinho.infrastructure.persistence.adapter;

import com.manolinho.domain.model.CambioTalla;
import com.manolinho.domain.repository.CambioTallaRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCambioTallaRepositoryAdapter implements CambioTallaRepository {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, CambioTalla> storage = new ConcurrentHashMap<>();

    @Override
    public CambioTalla save(CambioTalla cambioTalla) {
        Long id = cambioTalla.getId() != null ? cambioTalla.getId() : sequence.incrementAndGet();
        CambioTalla persisted = new CambioTalla(
                id,
                cambioTalla.getPedidoId(),
                cambioTalla.getProductId(),
                cambioTalla.getTallaOrigen(),
                cambioTalla.getTallaDestino(),
                cambioTalla.getUnidades(),
                cambioTalla.getFechaSolicitud(),
                cambioTalla.getEstado()
        );
        storage.put(id, persisted);
        return persisted;
    }
}
