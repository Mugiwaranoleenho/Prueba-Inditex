package com.manolinho.infrastructure.persistence.adapter;

import com.manolinho.domain.repository.StockRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryStockRepositoryAdapter implements StockRepository {

    private final Map<String, Integer> storage = new ConcurrentHashMap<>();

    public InMemoryStockRepositoryAdapter() {
        storage.put(key(35455L, "S"), 20);
        storage.put(key(35455L, "M"), 20);
        storage.put(key(35455L, "L"), 20);
    }

    @Override
    public int getAvailableUnits(Long productId, String talla) {
        return storage.getOrDefault(key(productId, talla), 0);
    }

    @Override
    public void increase(Long productId, String talla, int units) {
        storage.merge(key(productId, talla), units, Integer::sum);
    }

    @Override
    public void decrease(Long productId, String talla, int units) {
        String key = key(productId, talla);
        int current = storage.getOrDefault(key, 0);
        if (current < units) {
            throw new IllegalArgumentException("Stock insuficiente para operar");
        }
        storage.put(key, current - units);
    }

    private String key(Long productId, String talla) {
        return productId + "|" + talla.trim().toUpperCase();
    }
}
