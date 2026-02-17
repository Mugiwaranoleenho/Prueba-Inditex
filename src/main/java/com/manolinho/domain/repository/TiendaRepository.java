package com.manolinho.domain.repository;

import com.manolinho.domain.model.Tienda;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TiendaRepository {

    Tienda save(Tienda tienda);

    Optional<Tienda> findApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId);
}
