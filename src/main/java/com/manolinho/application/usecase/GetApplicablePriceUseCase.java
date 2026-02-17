package com.manolinho.application.usecase;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GetApplicablePriceUseCase {

    private final TiendaRepository tiendaRepository;

    public GetApplicablePriceUseCase(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    public Optional<Tienda> execute(LocalDateTime applicationDate, Long productId, Long brandId) {
        return tiendaRepository.findApplicablePrice(applicationDate, productId, brandId);
    }
}
