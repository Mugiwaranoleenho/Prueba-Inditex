package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateTiendaUseCase;
import com.manolinho.application.usecase.GetApplicablePriceUseCase;
import com.manolinho.domain.model.Tienda;
import com.manolinho.infrastructure.security.RoleDiscountService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.manolinho.infrastructure.util.AppConstants.Endpoint.PRECIO_RELATIVE;
import static com.manolinho.infrastructure.util.AppConstants.Endpoint.TIENDAS_BASE;
import static com.manolinho.infrastructure.util.AppConstants.Message.LOG_GET_TIENDAS_PRECIO;
import static com.manolinho.infrastructure.util.AppConstants.Message.LOG_POST_TIENDAS;
import static com.manolinho.infrastructure.util.AppConstants.Message.NO_HAY_TARIFA_APLICABLE;
import static com.manolinho.infrastructure.util.AppConstants.RequestParam.BRAND_ID;
import static com.manolinho.infrastructure.util.AppConstants.RequestParam.FECHA_APLICACION;
import static com.manolinho.infrastructure.util.AppConstants.RequestParam.PRODUCT_ID;
import static com.manolinho.infrastructure.util.AppConstants.Security.PREAUTH_EMPLEADO_JEFE_ADMIN;

@RestController
@RequestMapping(TIENDAS_BASE)
@Slf4j
public class TiendaController {

    private final CreateTiendaUseCase createTiendaUseCase;
    private final GetApplicablePriceUseCase getApplicablePriceUseCase;
    private final RoleDiscountService roleDiscountService;

    public TiendaController(CreateTiendaUseCase createTiendaUseCase,
                            GetApplicablePriceUseCase getApplicablePriceUseCase,
                            RoleDiscountService roleDiscountService) {
        this.createTiendaUseCase = createTiendaUseCase;
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
        this.roleDiscountService = roleDiscountService;
    }

    @PostMapping
    @PreAuthorize(PREAUTH_EMPLEADO_JEFE_ADMIN)
    public Tienda create(@RequestBody CreateTiendaRequest request) {
        log.info(LOG_POST_TIENDAS, request.brandId(), request.productId());
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

    @GetMapping(PRECIO_RELATIVE)
    public ApplicablePriceResponse getApplicablePrice(
            @RequestParam(FECHA_APLICACION)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAplicacion,
            @RequestParam(PRODUCT_ID) Long productId,
            @RequestParam(BRAND_ID) Long brandId,
            Authentication authentication
    ) {
        log.info(LOG_GET_TIENDAS_PRECIO, fechaAplicacion, productId, brandId);
        return getApplicablePriceUseCase
                .execute(fechaAplicacion, productId, brandId)
                .map(tienda -> {
                    BigDecimal discountPercent = roleDiscountService.resolveDiscountPercent(authentication);
                    BigDecimal finalPrice = roleDiscountService.applyDiscount(tienda.getPrice(), discountPercent);
                    return new ApplicablePriceResponse(
                            tienda.getProductId(),
                            tienda.getBrandId(),
                            tienda.getPriceList(),
                            tienda.getStartDate(),
                            tienda.getEndDate(),
                            tienda.getPrice(),
                            discountPercent,
                            finalPrice
                    );
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NO_HAY_TARIFA_APLICABLE));
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
            BigDecimal originalPrice,
            BigDecimal discountPercent,
            BigDecimal finalPrice
    ) {
    }
}
