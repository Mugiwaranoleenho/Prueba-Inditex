package com.manolinho.application.usecase;

import com.manolinho.domain.model.CambioTalla;
import com.manolinho.domain.model.Pedido;
import com.manolinho.domain.repository.CambioTallaRepository;
import com.manolinho.domain.repository.PedidoRepository;
import com.manolinho.domain.repository.StockRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateCambioTallaUseCase {

    private static final int MAX_DIAS_CAMBIO = 30;
    private static final String ESTADO_COMPLETADO = "COMPLETADO";

    private final PedidoRepository pedidoRepository;
    private final StockRepository stockRepository;
    private final CambioTallaRepository cambioTallaRepository;

    public CreateCambioTallaUseCase(PedidoRepository pedidoRepository,
                                    StockRepository stockRepository,
                                    CambioTallaRepository cambioTallaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.stockRepository = stockRepository;
        this.cambioTallaRepository = cambioTallaRepository;
    }

    public CambioTalla execute(Long pedidoId,
                               Long productId,
                               String tallaOrigen,
                               String tallaDestino,
                               Integer unidades,
                               LocalDateTime fechaSolicitud) {
        log.info("Procesando cambio de talla: pedidoId={}, productId={}, {}->{}, unidades={}",
                pedidoId, productId, tallaOrigen, tallaDestino, unidades);
        validateInput(pedidoId, productId, tallaOrigen, tallaDestino, unidades, fechaSolicitud);
        String origenNormalizada = tallaOrigen.trim().toUpperCase();
        String destinoNormalizada = tallaDestino.trim().toUpperCase();

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        long diasDesdeCompra = ChronoUnit.DAYS.between(pedido.getFechaCompra().toLocalDate(), fechaSolicitud.toLocalDate());
        if (diasDesdeCompra > MAX_DIAS_CAMBIO) {
            throw new IllegalArgumentException("Ventana de cambio de talla superada (maximo 30 dias)");
        }
        if (diasDesdeCompra < 0) {
            throw new IllegalArgumentException("fechaSolicitud no puede ser anterior a fechaCompra");
        }

        int unidadesCompradas = pedido.getLineas().stream()
                .filter(linea -> linea.getProductId().equals(productId) && linea.getTalla().equalsIgnoreCase(origenNormalizada))
                .mapToInt(linea -> linea.getUnidades())
                .sum();

        if (unidadesCompradas == 0) {
            throw new IllegalArgumentException("No existe linea comprada para productId y talla origen");
        }
        if (unidades > unidadesCompradas) {
            throw new IllegalArgumentException("UNIDADES solicitadas superan las compradas");
        }

        int disponiblesDestino = stockRepository.getAvailableUnits(productId, destinoNormalizada);
        if (disponiblesDestino < unidades) {
            throw new IllegalArgumentException("Stock insuficiente para talla destino");
        }

        stockRepository.decrease(productId, destinoNormalizada, unidades);
        stockRepository.increase(productId, origenNormalizada, unidades);

        CambioTalla cambioTalla = new CambioTalla(
                null,
                pedidoId,
                productId,
                origenNormalizada,
                destinoNormalizada,
                unidades,
                fechaSolicitud,
                ESTADO_COMPLETADO
        );

        CambioTalla saved = cambioTallaRepository.save(cambioTalla);
        log.info("Cambio de talla completado id={}, pedidoId={}", saved.getId(), saved.getPedidoId());
        return saved;
    }

    private void validateInput(Long pedidoId,
                               Long productId,
                               String tallaOrigen,
                               String tallaDestino,
                               Integer unidades,
                               LocalDateTime fechaSolicitud) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("pedidoId debe ser mayor que 0");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId debe ser mayor que 0");
        }
        if (tallaOrigen == null || tallaOrigen.isBlank()) {
            throw new IllegalArgumentException("tallaOrigen obligatoria");
        }
        if (tallaDestino == null || tallaDestino.isBlank()) {
            throw new IllegalArgumentException("tallaDestino obligatoria");
        }
        if (tallaOrigen.equalsIgnoreCase(tallaDestino)) {
            throw new IllegalArgumentException("tallaDestino debe ser distinta de tallaOrigen");
        }
        if (unidades == null || unidades <= 0) {
            throw new IllegalArgumentException("UNIDADES debe ser mayor que 0");
        }
        if (fechaSolicitud == null) {
            throw new IllegalArgumentException("fechaSolicitud obligatoria");
        }
    }
}
