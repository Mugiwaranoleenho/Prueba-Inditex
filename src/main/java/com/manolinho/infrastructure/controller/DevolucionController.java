package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateDevolucionUseCase;
import com.manolinho.application.usecase.CreateDevolucionUseCase.LineaDevolucionInput;
import com.manolinho.domain.model.Devolucion;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.manolinho.infrastructure.util.AppConstants.Mensajes.LOG_POST_DEVOLUCIONES;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.DEVOLUCIONES_BASE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.PREAUTORIZACION_CLIENTE_EMPLEADO_JEFE_ADMIN;

@RestController
@RequestMapping(DEVOLUCIONES_BASE)
@Slf4j
public class DevolucionController {

    private final CreateDevolucionUseCase createDevolucionUseCase;

    public DevolucionController(CreateDevolucionUseCase createDevolucionUseCase) {
        this.createDevolucionUseCase = createDevolucionUseCase;
    }

    @PostMapping
    @PreAuthorize(PREAUTORIZACION_CLIENTE_EMPLEADO_JEFE_ADMIN)
    public DevolucionResponse crear(@RequestBody CreateDevolucionRequest solicitud) {
        log.info(LOG_POST_DEVOLUCIONES, solicitud.pedidoId(), solicitud.lineas() == null ? 0 : solicitud.lineas().size());
        Devolucion devolucion = createDevolucionUseCase.execute(
                solicitud.pedidoId(),
                solicitud.fechaSolicitud(),
                solicitud.lineas() == null ? null : solicitud.lineas().stream()
                .map(linea -> new LineaDevolucionInput(linea.productId(), linea.talla(), linea.unidades()))
                .toList()
        );

        return new DevolucionResponse(
                devolucion.getId(),
                devolucion.getPedidoId(),
                devolucion.getFechaSolicitud(),
                devolucion.getImporteReembolso(),
                devolucion.getEstado(),
                devolucion.getLineasDevueltas().stream()
                        .map(linea -> new DevolucionLineaResponse(
                                linea.getProductId(),
                                linea.getTalla(),
                                linea.getUnidades(),
                                linea.getPrecioUnitario(),
                                linea.getSubtotal()
                        ))
                        .toList()
        );
    }

    public record CreateDevolucionRequest(Long pedidoId, LocalDateTime fechaSolicitud, List<DevolucionLineaRequest> lineas) {
    }

    public record DevolucionLineaRequest(Long productId, String talla, Integer unidades) {
    }

    public record DevolucionResponse(
            Long id,
            Long pedidoId,
            LocalDateTime fechaSolicitud,
            BigDecimal importeReembolso,
            String estado,
            List<DevolucionLineaResponse> lineas
    ) {
    }

    public record DevolucionLineaResponse(
            Long productId,
            String talla,
            Integer unidades,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {
    }
}
