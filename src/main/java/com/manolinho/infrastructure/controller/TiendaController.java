package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateTiendaUseCase;
import com.manolinho.application.usecase.GetApplicablePriceUseCase;
import com.manolinho.domain.model.Tienda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/tiendas")
public class TiendaController {

    private final CreateTiendaUseCase createTiendaUseCase;
    private final GetApplicablePriceUseCase getApplicablePriceUseCase;

    public TiendaController(CreateTiendaUseCase createTiendaUseCase,
                            GetApplicablePriceUseCase getApplicablePriceUseCase) {
        this.createTiendaUseCase = createTiendaUseCase;
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
    }

    @PostMapping
    public Tienda create(@RequestBody CreateTiendaRequest request) {
        return createTiendaUseCase.execute(
                request.brandId(),
                request.startDate(),
                request.endDate(),
                request.priceList(),
                request.productId(),
                request.priority(),
                request.price(),
                request.curr()
        );
    }

    @GetMapping("/precio")
    public ApplicablePriceResponse getApplicablePrice(
            @RequestParam("fechaAplicacion")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAplicacion,
            @RequestParam("productId") Long productId,
            @RequestParam("brandId") Long brandId
    ) {
        return getApplicablePriceUseCase
                .execute(fechaAplicacion, productId, brandId)
                .map(tienda -> new ApplicablePriceResponse(
                        tienda.getProductId(),
                        tienda.getBrandId(),
                        tienda.getPriceList(),
                        tienda.getStartDate(),
                        tienda.getEndDate(),
                        tienda.getPrice()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay tarifa aplicable"));
    }

    public record CreateTiendaRequest(
            Long brandId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer priceList,
            Long productId,
            Integer priority,
            BigDecimal price,
            String curr
    ) {
    }

    public record ApplicablePriceResponse(
            Long productId,
            Long brandId,
            Integer priceList,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal price
    ) {
    }
}
