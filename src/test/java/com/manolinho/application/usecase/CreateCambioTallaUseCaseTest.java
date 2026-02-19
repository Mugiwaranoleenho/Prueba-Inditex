package com.manolinho.application.usecase;

import com.manolinho.domain.model.CambioTalla;
import com.manolinho.domain.model.LineaPedido;
import com.manolinho.domain.model.Pedido;
import com.manolinho.domain.repository.CambioTallaRepository;
import com.manolinho.domain.repository.PedidoRepository;
import com.manolinho.domain.repository.StockRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCambioTallaUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CambioTallaRepository cambioTallaRepository;

    @InjectMocks
    private CreateCambioTallaUseCase createCambioTallaUseCase;

    @Test
    void deberiaCrearCambioTallaSiHayStock() {
        Pedido pedido = new Pedido(
                1L,
                "PED-001",
                LocalDateTime.parse("2026-02-01T10:00:00"),
                List.of(new LineaPedido(35455L, "M", 2, new BigDecimal("19.99"))),
                new BigDecimal("39.98"),
                "COMPLETADO"
        );
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(stockRepository.getAvailableUnits(35455L, "L")).thenReturn(5);
        when(cambioTallaRepository.save(any(CambioTalla.class)))
                .thenAnswer(invocation -> {
                    CambioTalla c = invocation.getArgument(0);
                    return new CambioTalla(9L, c.getPedidoId(), c.getProductId(), c.getTallaOrigen(), c.getTallaDestino(),
                            c.getUnidades(), c.getFechaSolicitud(), c.getEstado());
                });

        CambioTalla result = createCambioTallaUseCase.execute(
                1L,
                35455L,
                "M",
                "L",
                1,
                LocalDateTime.parse("2026-02-10T11:00:00")
        );

        assertEquals(9L, result.getId());
        assertEquals("COMPLETADO", result.getEstado());
    }

    @Test
    void deberiaFallarSiNoHayStockDestino() {
        Pedido pedido = new Pedido(
                1L,
                "PED-001",
                LocalDateTime.parse("2026-02-01T10:00:00"),
                List.of(new LineaPedido(35455L, "M", 2, new BigDecimal("19.99"))),
                new BigDecimal("39.98"),
                "COMPLETADO"
        );
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(stockRepository.getAvailableUnits(35455L, "L")).thenReturn(0);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createCambioTallaUseCase.execute(
                        1L,
                        35455L,
                        "M",
                        "L",
                        1,
                        LocalDateTime.parse("2026-02-10T11:00:00")
                )
        );
        assertEquals("Stock insuficiente para talla destino", ex.getMessage());
    }
}
