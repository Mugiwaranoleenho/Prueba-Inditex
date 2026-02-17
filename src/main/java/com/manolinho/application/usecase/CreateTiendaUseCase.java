package com.manolinho.application.usecase;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreateTiendaUseCase {

    private final TiendaRepository tiendaRepository;

    public CreateTiendaUseCase(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    public Tienda execute(Long brandId,
                          LocalDateTime startDate,
                          LocalDateTime endDate,
                          Integer priceList,
                          Long productId,
                          Integer priority,
                          BigDecimal price,
                          String curr) {
        validate(brandId, startDate, endDate, priceList, productId, priority, price, curr);
        Tienda tienda = new Tienda(
                null,
                brandId,
                startDate,
                endDate,
                priceList,
                productId,
                priority,
                price,
                curr
        );
        return tiendaRepository.save(tienda);
    }

    private void validate(Long brandId,
                          LocalDateTime startDate,
                          LocalDateTime endDate,
                          Integer priceList,
                          Long productId,
                          Integer priority,
                          BigDecimal price,
                          String curr) {
        if (brandId == null) {
            throw new IllegalArgumentException("BRAND_ID obligatorio");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("START_DATE y END_DATE obligatorios");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("END_DATE no puede ser anterior a START_DATE");
        }
        if (priceList == null) {
            throw new IllegalArgumentException("PRICE_LIST obligatorio");
        }
        if (productId == null) {
            throw new IllegalArgumentException("PRODUCT_ID obligatorio");
        }
        if (priority == null) {
            throw new IllegalArgumentException("PRIORITY obligatorio");
        }
        if (price == null) {
            throw new IllegalArgumentException("PRICE obligatorio");
        }
        if (curr == null || curr.isBlank()) {
            throw new IllegalArgumentException("CURR obligatorio");
        }
    }
}
