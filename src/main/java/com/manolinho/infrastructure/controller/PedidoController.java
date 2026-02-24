package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreatePedidoUseCase;
import com.manolinho.application.usecase.CreatePedidoUseCase.CreatePedidoLineaInput;
import com.manolinho.domain.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.manolinho.infrastructure.util.AppConstants.Endpoint.PEDIDOS_BASE;
import static com.manolinho.infrastructure.util.AppConstants.Message.LOG_POST_PEDIDOS;
import static com.manolinho.infrastructure.util.AppConstants.Security.PREAUTH_CLIENTE_EMPLEADO_JEFE_ADMIN;

@RestController
@RequestMapping(PEDIDOS_BASE)
@Slf4j
public class PedidoController {

    private final CreatePedidoUseCase createPedidoUseCase;

    public PedidoController(CreatePedidoUseCase createPedidoUseCase) {
        this.createPedidoUseCase = createPedidoUseCase;
    }

    @PostMapping
    @PreAuthorize(PREAUTH_CLIENTE_EMPLEADO_JEFE_ADMIN)
    public PedidoResponse create(@RequestBody CreatePedidoRequest request) {
        log.info(LOG_POST_PEDIDOS, request.fechaCompra(), request.lineas() == null ? 0 : request.lineas().size());
        Pedido pedido = createPedidoUseCase.execute(
                request.fechaCompra(),
                request.lineas() == null ? null : request.lineas().stream()
                .map(linea -> new CreatePedidoLineaInput(
                        linea.productId(),
                        linea.talla(),
                        linea.unidades(),
                        linea.precioUnitario()
                ))
                .toList()
        );

        return new PedidoResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getFechaCompra(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getLineas().stream()
                        .map(linea -> new PedidoLineaResponse(
                                linea.getProductId(),
                                linea.getTalla(),
                                linea.getUnidades(),
                                linea.getPrecioUnitario(),
                                linea.getSubtotal()
                        ))
                        .toList()
        );
    }

    public record CreatePedidoRequest(
            LocalDateTime fechaCompra,
            List<CreatePedidoLineaRequest> lineas
    ) {
    }

    public record CreatePedidoLineaRequest(
            Long productId,
            String talla,
            Integer unidades,
            BigDecimal precioUnitario
    ) {
    }

    public record PedidoResponse(
            Long id,
            String numeroPedido,
            LocalDateTime fechaCompra,
            BigDecimal total,
            String estado,
            List<PedidoLineaResponse> lineas
    ) {
    }

    public record PedidoLineaResponse(
            Long productId,
            String talla,
            Integer unidades,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {
    }
}
