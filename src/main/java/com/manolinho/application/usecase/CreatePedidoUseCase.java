package com.manolinho.application.usecase;

import com.manolinho.domain.model.LineaPedido;
import com.manolinho.domain.model.Pedido;
import com.manolinho.domain.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreatePedidoUseCase {

    private static final String ESTADO_COMPLETADO = "COMPLETADO";

    private final PedidoRepository pedidoRepository;

    public CreatePedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido execute(LocalDateTime fechaCompra, List<CreatePedidoLineaInput> lineasInput) {
        log.info("Creando pedido para fechaCompra={} con {} lineas", fechaCompra, lineasInput == null ? 0 : lineasInput.size());
        validate(fechaCompra, lineasInput);

        List<LineaPedido> lineas = lineasInput.stream()
                .map(linea -> new LineaPedido(
                        linea.productId(),
                        linea.talla().trim().toUpperCase(),
                        linea.unidades(),
                        linea.precioUnitario()
                ))
                .toList();

        BigDecimal total = lineas.stream()
                .map(LineaPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = new Pedido(
                null,
                UUID.randomUUID().toString(),
                fechaCompra,
                lineas,
                total,
                ESTADO_COMPLETADO
        );

        Pedido saved = pedidoRepository.save(pedido);
        log.info("Pedido creado id={}, numero={}", saved.getId(), saved.getNumeroPedido());
        return saved;
    }

    private void validate(LocalDateTime fechaCompra, List<CreatePedidoLineaInput> lineasInput) {
        if (fechaCompra == null) {
            throw new IllegalArgumentException("fechaCompra obligatoria");
        }
        if (lineasInput == null || lineasInput.isEmpty()) {
            throw new IllegalArgumentException("Debe existir al menos una linea de pedido");
        }

        for (CreatePedidoLineaInput linea : lineasInput) {
            if (linea.productId() == null || linea.productId() <= 0) {
                throw new IllegalArgumentException("PRODUCT_ID debe ser mayor que 0");
            }
            if (linea.talla() == null || linea.talla().isBlank()) {
                throw new IllegalArgumentException("TALLA obligatoria");
            }
            if (linea.unidades() == null || linea.unidades() <= 0) {
                throw new IllegalArgumentException("UNIDADES debe ser mayor que 0");
            }
            if (linea.precioUnitario() == null || linea.precioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("PRECIO_UNITARIO debe ser mayor que 0");
            }
        }
    }

    public record CreatePedidoLineaInput(
            Long productId,
            String talla,
            Integer unidades,
            BigDecimal precioUnitario
    ) {
    }
}
