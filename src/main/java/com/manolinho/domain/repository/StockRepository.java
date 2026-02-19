package com.manolinho.domain.repository;

public interface StockRepository {

    int getAvailableUnits(Long productId, String talla);

    void increase(Long productId, String talla, int units);

    void decrease(Long productId, String talla, int units);
}
