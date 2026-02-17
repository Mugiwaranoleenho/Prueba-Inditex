package com.manolinho.application.usecase;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTiendaUseCaseTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @InjectMocks
    private CreateTiendaUseCase createTiendaUseCase;

    @Test
    void deberiaGuardarYRetornarTiendaCreada() {
        when(tiendaRepository.save(any(Tienda.class)))
                .thenReturn(new Tienda(
                        10L,
                        1L,
                        LocalDateTime.parse("2026-01-01T00:00:00"),
                        LocalDateTime.parse("2026-12-31T23:59:59"),
                        2,
                        12345L,
                        1,
                        new BigDecimal("25.45"),
                        "EUR"
                ));

        Tienda result = createTiendaUseCase.execute(
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-12-31T23:59:59"),
                2,
                12345L,
                1,
                new BigDecimal("25.45"),
                "EUR"
        );

        ArgumentCaptor<Tienda> captor = ArgumentCaptor.forClass(Tienda.class);
        verify(tiendaRepository).save(captor.capture());
        Tienda enviada = captor.getValue();

        assertEquals(2, enviada.getPriceList());
        assertEquals(12345L, enviada.getProductId());
        assertEquals(new BigDecimal("25.45"), enviada.getPrice());
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getBrandId());
    }

    @Test
    void deberiaLanzarExcepcionCuandoBrandIdEsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createTiendaUseCase.execute(
                        null,
                        LocalDateTime.parse("2026-01-01T00:00:00"),
                        LocalDateTime.parse("2026-12-31T23:59:59"),
                        2,
                        12345L,
                        1,
                        new BigDecimal("25.45"),
                        "EUR"
                )
        );
        assertEquals("BRAND_ID obligatorio", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoEndDateEsAnterior() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createTiendaUseCase.execute(
                        1L,
                        LocalDateTime.parse("2026-12-31T23:59:59"),
                        LocalDateTime.parse("2026-01-01T00:00:00"),
                        2,
                        12345L,
                        1,
                        new BigDecimal("25.45"),
                        "EUR"
                )
        );
        assertEquals("END_DATE no puede ser anterior a START_DATE", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoPriceNoEsPositivo() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createTiendaUseCase.execute(
                        1L,
                        LocalDateTime.parse("2026-01-01T00:00:00"),
                        LocalDateTime.parse("2026-12-31T23:59:59"),
                        2,
                        12345L,
                        1,
                        BigDecimal.ZERO,
                        "EUR"
                )
        );
        assertEquals("PRICE debe ser mayor que 0", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCurrNoEsIso4217() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createTiendaUseCase.execute(
                        1L,
                        LocalDateTime.parse("2026-01-01T00:00:00"),
                        LocalDateTime.parse("2026-12-31T23:59:59"),
                        2,
                        12345L,
                        1,
                        new BigDecimal("10.00"),
                        "EURO"
                )
        );
        assertEquals("CURR debe ser un codigo ISO 4217 valido", ex.getMessage());
    }

    @Test
    void deberiaNormalizarCurrAMayusculas() {
        when(tiendaRepository.save(any(Tienda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Tienda.class));

        Tienda result = createTiendaUseCase.execute(
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-12-31T23:59:59"),
                2,
                12345L,
                1,
                new BigDecimal("25.45"),
                "eur"
        );

        assertEquals("EUR", result.getCurr());
    }
}
