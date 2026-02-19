package com.manolinho.application.usecase;

import com.manolinho.application.usecase.CreateDevolucionUseCase.LineaDevolucionInput;
import com.manolinho.domain.model.Devolucion;
import com.manolinho.domain.model.LineaPedido;
import com.manolinho.domain.model.Pedido;
import com.manolinho.domain.repository.DevolucionRepository;
import com.manolinho.domain.repository.PedidoRepository;
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
class CreateDevolucionUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DevolucionRepository devolucionRepository;

    @InjectMocks
    private CreateDevolucionUseCase createDevolucionUseCase;

    @Test
    void deberiaCrearDevolucionDentroDeVentana() {
        Pedido pedido = new Pedido(
                1L,
                "PED-001",
                LocalDateTime.parse("2026-02-01T10:00:00"),
                List.of(new LineaPedido(35455L, "M", 2, new BigDecimal("19.99"))),
                new BigDecimal("39.98"),
                "COMPLETADO"
        );
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(devolucionRepository.save(any(Devolucion.class)))
                .thenAnswer(invocation -> {
                    Devolucion d = invocation.getArgument(0);
                    return new Devolucion(10L, d.getPedidoId(), d.getFechaSolicitud(), d.getLineasDevueltas(), d.getImporteReembolso(), d.getEstado());
                });

        Devolucion result = createDevolucionUseCase.execute(
                1L,
                LocalDateTime.parse("2026-02-10T12:00:00"),
                List.of(new LineaDevolucionInput(35455L, "m", 1))
        );

        assertEquals(10L, result.getId());
        assertEquals(new BigDecimal("19.99"), result.getImporteReembolso());
        assertEquals("APROBADA", result.getEstado());
    }

    @Test
    void deberiaFallarCuandoVentanaEstaSuperada() {
        Pedido pedido = new Pedido(
                1L,
                "PED-001",
                LocalDateTime.parse("2026-01-01T10:00:00"),
                List.of(new LineaPedido(35455L, "M", 2, new BigDecimal("19.99"))),
                new BigDecimal("39.98"),
                "COMPLETADO"
        );
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createDevolucionUseCase.execute(
                        1L,
                        LocalDateTime.parse("2026-02-20T10:00:00"),
                        List.of(new LineaDevolucionInput(35455L, "M", 1))
                )
        );
        assertEquals("Ventana de devolucion superada (maximo 30 dias)", ex.getMessage());
    }
}
