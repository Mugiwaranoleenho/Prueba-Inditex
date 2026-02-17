package com.manolinho.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TiendaTest {

    @Test
    void deberiaCrearTiendaCuandoNombreEsValido() {
        Tienda tienda = new Tienda(
                1L,
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-12-31T23:59:59"),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                "EUR"
        );

        assertEquals(1L, tienda.getId());
        assertEquals(1L, tienda.getBrandId());
        assertEquals(1, tienda.getPriceList());
        assertEquals(35455L, tienda.getProductId());
        assertEquals(0, tienda.getPriority());
        assertEquals(new BigDecimal("35.50"), tienda.getPrice());
        assertEquals("EUR", tienda.getCurr());
    }

    @Test
    void deberiaPermitirConstruccionSinValidarEnDominio() {
        Tienda tienda = new Tienda(
                null,
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-12-31T23:59:59"),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                "EUR"
        );

        assertNull(tienda.getId());
    }
}
