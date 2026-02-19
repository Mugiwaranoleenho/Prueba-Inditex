package com.manolinho.application.usecase;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class GetApplicablePriceUseCase {

    private final TiendaRepository tiendaRepository;

    public GetApplicablePriceUseCase(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    public Optional<Tienda> execute(LocalDateTime applicationDate, Long productId, Long brandId) {
        log.debug("Buscando tarifa aplicable: fecha={}, productId={}, brandId={}", applicationDate, productId, brandId);
        if (applicationDate == null) {
            throw new IllegalArgumentException("fechaAplicacion obligatoria");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("PRODUCT_ID debe ser mayor que 0");
        }
        if (brandId == null || brandId <= 0) {
            throw new IllegalArgumentException("BRAND_ID debe ser mayor que 0");
        }
        Optional<Tienda> result = tiendaRepository.findApplicablePrice(applicationDate, productId, brandId);
        log.debug("Tarifa aplicable encontrada={}", result.isPresent());
        return result;
    }
}
