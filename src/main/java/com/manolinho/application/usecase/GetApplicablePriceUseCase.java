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
        if (applicationDate == null) {
            throw new IllegalArgumentException("fechaAplicacion obligatoria");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("PRODUCT_ID debe ser mayor que 0");
        }
        if (brandId == null || brandId <= 0) {
            throw new IllegalArgumentException("BRAND_ID debe ser mayor que 0");
        }
        return tiendaRepository.findApplicablePrice(applicationDate, productId, brandId);
    }
}
