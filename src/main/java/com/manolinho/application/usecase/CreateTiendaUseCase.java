package com.manolinho.application.usecase;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CreateTiendaUseCase {

    private static final Set<String> ISO_4217_CODES = Currency.getAvailableCurrencies()
            .stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

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
        log.info("Creando tarifa: brandId={}, productId={}, priceList={}", brandId, productId, priceList);
        validate(brandId, startDate, endDate, priceList, productId, priority, price, curr);
        String normalizedCurrency = curr.trim().toUpperCase(Locale.ROOT);
        Tienda tienda = new Tienda(
                null,
                brandId,
                startDate,
                endDate,
                priceList,
                productId,
                priority,
                price,
                normalizedCurrency
        );
        Tienda saved = tiendaRepository.save(tienda);
        log.info("Tarifa creada con id={}", saved.getId());
        return saved;
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
        if (brandId <= 0) {
            throw new IllegalArgumentException("BRAND_ID debe ser mayor que 0");
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
        if (priceList <= 0) {
            throw new IllegalArgumentException("PRICE_LIST debe ser mayor que 0");
        }
        if (productId == null) {
            throw new IllegalArgumentException("PRODUCT_ID obligatorio");
        }
        if (productId <= 0) {
            throw new IllegalArgumentException("PRODUCT_ID debe ser mayor que 0");
        }
        if (priority == null) {
            throw new IllegalArgumentException("PRIORITY obligatorio");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("PRIORITY no puede ser negativo");
        }
        if (price == null) {
            throw new IllegalArgumentException("PRICE obligatorio");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("PRICE debe ser mayor que 0");
        }
        if (curr == null || curr.isBlank()) {
            throw new IllegalArgumentException("CURR obligatorio");
        }
        String normalizedCurrency = curr.trim().toUpperCase(Locale.ROOT);
        if (!ISO_4217_CODES.contains(normalizedCurrency)) {
            throw new IllegalArgumentException("CURR debe ser un codigo ISO 4217 valido");
        }
    }
}
