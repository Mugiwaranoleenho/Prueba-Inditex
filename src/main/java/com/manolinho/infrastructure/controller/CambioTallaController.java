package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateCambioTallaUseCase;
import com.manolinho.domain.model.CambioTalla;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cambios-talla")
@Slf4j
public class CambioTallaController {

    private final CreateCambioTallaUseCase createCambioTallaUseCase;

    public CambioTallaController(CreateCambioTallaUseCase createCambioTallaUseCase) {
        this.createCambioTallaUseCase = createCambioTallaUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE','EMPLEADO','EMPLEADO_JEFE','ADMIN')")
    public CambioTallaResponse create(@RequestBody CreateCambioTallaRequest request) {
        log.info("POST /cambios-talla pedidoId={}, productId={}", request.pedidoId(), request.productId());
        CambioTalla cambio = createCambioTallaUseCase.execute(
                request.pedidoId(),
                request.productId(),
                request.tallaOrigen(),
                request.tallaDestino(),
                request.unidades(),
                request.fechaSolicitud()
        );

        return new CambioTallaResponse(
                cambio.getId(),
                cambio.getPedidoId(),
                cambio.getProductId(),
                cambio.getTallaOrigen(),
                cambio.getTallaDestino(),
                cambio.getUnidades(),
                cambio.getFechaSolicitud(),
                cambio.getEstado()
        );
    }

    public record CreateCambioTallaRequest(
            Long pedidoId,
            Long productId,
            String tallaOrigen,
            String tallaDestino,
            Integer unidades,
            LocalDateTime fechaSolicitud
    ) {
    }

    public record CambioTallaResponse(
            Long id,
            Long pedidoId,
            Long productId,
            String tallaOrigen,
            String tallaDestino,
            Integer unidades,
            LocalDateTime fechaSolicitud,
            String estado
    ) {
    }
}
