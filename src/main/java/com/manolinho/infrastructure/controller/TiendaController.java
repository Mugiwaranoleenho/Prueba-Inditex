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

import static com.manolinho.infrastructure.util.AppConstants.Mensajes.LOG_GET_TIENDAS_PRECIO;
import static com.manolinho.infrastructure.util.AppConstants.Mensajes.LOG_POST_TIENDAS;
import static com.manolinho.infrastructure.util.AppConstants.Mensajes.NO_HAY_TARIFA_APLICABLE;
import static com.manolinho.infrastructure.util.AppConstants.Parametros.FECHA_APLICACION;
import static com.manolinho.infrastructure.util.AppConstants.Parametros.ID_MARCA;
import static com.manolinho.infrastructure.util.AppConstants.Parametros.ID_PRODUCTO;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.PRECIO_RELATIVA;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.TIENDAS_BASE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.PREAUTORIZACION_EMPLEADO_JEFE_ADMIN;

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
    @PreAuthorize(PREAUTORIZACION_EMPLEADO_JEFE_ADMIN)
    public Tienda crear(@RequestBody CreateTiendaRequest solicitud) {
        log.info(LOG_POST_TIENDAS, solicitud.brandId(), solicitud.productId());
        return createTiendaUseCase.execute(
                solicitud.brandId(),
                solicitud.startDate(),
                solicitud.endDate(),
                solicitud.priceList(),
                solicitud.productId(),
                solicitud.priority(),
                solicitud.price(),
                solicitud.curr()
        );
    }

    @GetMapping(PRECIO_RELATIVA)
    public ApplicablePriceResponse obtenerPrecioAplicable(
            @RequestParam(FECHA_APLICACION)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAplicacion,
            @RequestParam(ID_PRODUCTO) Long idProducto,
            @RequestParam(ID_MARCA) Long idMarca,
            Authentication autenticacion
    ) {
        log.info(LOG_GET_TIENDAS_PRECIO, fechaAplicacion, idProducto, idMarca);
        return getApplicablePriceUseCase
                .execute(fechaAplicacion, idProducto, idMarca)
                .map(tienda -> {
                    BigDecimal porcentajeDescuento = roleDiscountService.resolverPorcentajeDescuento(autenticacion);
                    BigDecimal precioFinal = roleDiscountService.aplicarDescuento(tienda.getPrice(), porcentajeDescuento);
                    return new ApplicablePriceResponse(
                            tienda.getProductId(),
                            tienda.getBrandId(),
                            tienda.getPriceList(),
                            tienda.getStartDate(),
                            tienda.getEndDate(),
                            tienda.getPrice(),
                            porcentajeDescuento,
                            precioFinal
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
