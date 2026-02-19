package com.manolinho.application.usecase;

import com.manolinho.domain.model.Devolucion;
import com.manolinho.domain.model.LineaPedido;
import com.manolinho.domain.model.Pedido;
import com.manolinho.domain.repository.DevolucionRepository;
import com.manolinho.domain.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateDevolucionUseCase {

    private static final int MAX_DIAS_DEVOLUCION = 30;
    private static final String ESTADO_APROBADA = "APROBADA";

    private final PedidoRepository pedidoRepository;
    private final DevolucionRepository devolucionRepository;

    public CreateDevolucionUseCase(PedidoRepository pedidoRepository, DevolucionRepository devolucionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.devolucionRepository = devolucionRepository;
    }

    public Devolucion execute(Long pedidoId, LocalDateTime fechaSolicitud, List<LineaDevolucionInput> lineasInput) {
        log.info("Creando devolucion para pedidoId={} con {} lineas", pedidoId, lineasInput == null ? 0 : lineasInput.size());
        validateInput(pedidoId, fechaSolicitud, lineasInput);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        long diasDesdeCompra = ChronoUnit.DAYS.between(pedido.getFechaCompra().toLocalDate(), fechaSolicitud.toLocalDate());
        if (diasDesdeCompra > MAX_DIAS_DEVOLUCION) {
            throw new IllegalArgumentException("Ventana de devolucion superada (maximo 30 dias)");
        }
        if (diasDesdeCompra < 0) {
            throw new IllegalArgumentException("fechaSolicitud no puede ser anterior a fechaCompra");
        }

        Map<String, LineaPedido> lineasCompraPorClave = pedido.getLineas().stream()
                .collect(Collectors.toMap(
                        linea -> key(linea.getProductId(), linea.getTalla()),
                        linea -> linea
                ));

        List<LineaPedido> lineasDevueltas = lineasInput.stream()
                .map(lineaInput -> {
                    String tallaNormalizada = lineaInput.talla().trim().toUpperCase();
                    String clave = key(lineaInput.productId(), tallaNormalizada);
                    LineaPedido compra = lineasCompraPorClave.get(clave);
                    if (compra == null) {
                        throw new IllegalArgumentException("Linea no encontrada en pedido: productId=" + lineaInput.productId() + ", talla=" + tallaNormalizada);
                    }
                    if (lineaInput.unidades() > compra.getUnidades()) {
                        throw new IllegalArgumentException("Unidades a devolver superiores a las compradas para productId=" + lineaInput.productId() + ", talla=" + tallaNormalizada);
                    }
                    return new LineaPedido(compra.getProductId(), compra.getTalla(), lineaInput.unidades(), compra.getPrecioUnitario());
                })
                .toList();

        BigDecimal reembolso = lineasDevueltas.stream()
                .map(LineaPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Devolucion devolucion = new Devolucion(
                null,
                pedidoId,
                fechaSolicitud,
                lineasDevueltas,
                reembolso,
                ESTADO_APROBADA
        );

        Devolucion saved = devolucionRepository.save(devolucion);
        log.info("Devolucion creada id={}, pedidoId={}, reembolso={}", saved.getId(), saved.getPedidoId(), saved.getImporteReembolso());
        return saved;
    }

    private void validateInput(Long pedidoId, LocalDateTime fechaSolicitud, List<LineaDevolucionInput> lineasInput) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("pedidoId debe ser mayor que 0");
        }
        if (fechaSolicitud == null) {
            throw new IllegalArgumentException("fechaSolicitud obligatoria");
        }
        if (lineasInput == null || lineasInput.isEmpty()) {
            throw new IllegalArgumentException("Debe existir al menos una linea de devolucion");
        }
        for (LineaDevolucionInput linea : lineasInput) {
            if (linea.productId() == null || linea.productId() <= 0) {
                throw new IllegalArgumentException("PRODUCT_ID debe ser mayor que 0");
            }
            if (linea.talla() == null || linea.talla().isBlank()) {
                throw new IllegalArgumentException("TALLA obligatoria");
            }
            if (linea.unidades() == null || linea.unidades() <= 0) {
                throw new IllegalArgumentException("UNIDADES debe ser mayor que 0");
            }
        }
    }

    private String key(Long productId, String talla) {
        return productId + "|" + talla;
    }

    public record LineaDevolucionInput(Long productId, String talla, Integer unidades) {
    }
}
