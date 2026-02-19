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

@RestController
@RequestMapping("/devoluciones")
@Slf4j
public class DevolucionController {

    private final CreateDevolucionUseCase createDevolucionUseCase;

    public DevolucionController(CreateDevolucionUseCase createDevolucionUseCase) {
        this.createDevolucionUseCase = createDevolucionUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE','EMPLEADO','EMPLEADO_JEFE','ADMIN')")
    public DevolucionResponse create(@RequestBody CreateDevolucionRequest request) {
        log.info("POST /devoluciones pedidoId={}, lineas={}", request.pedidoId(), request.lineas() == null ? 0 : request.lineas().size());
        Devolucion devolucion = createDevolucionUseCase.execute(
                request.pedidoId(),
                request.fechaSolicitud(),
                request.lineas() == null ? null : request.lineas().stream()
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
