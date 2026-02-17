package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateTiendaUseCase;
import com.manolinho.application.usecase.GetApplicablePriceUseCase;
import com.manolinho.domain.model.Tienda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TiendaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TiendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateTiendaUseCase createTiendaUseCase;

    @MockBean
    private GetApplicablePriceUseCase getApplicablePriceUseCase;

    @Test
    void deberiaCrearTiendaConPost() throws Exception {
        when(createTiendaUseCase.execute(
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-06-30T23:59:59"),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                "EUR"
        )).thenReturn(new Tienda(
                1L,
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-06-30T23:59:59"),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                "EUR"
        ));

        String body = """
                {
                  "brandId": 1,
                  "startDate": "2026-01-01T00:00:00",
                  "endDate": "2026-06-30T23:59:59",
                  "priceList": 1,
                  "productId": 35455,
                  "priority": 0,
                  "price": 35.50,
                  "curr": "EUR"
                }
                """;

        mockMvc.perform(post("/tiendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.priority").value(0))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.direccion").doesNotExist());
    }

    @Test
    void deberiaConsultarPrecioAplicablePorFechaProductoYCadena() throws Exception {
        when(getApplicablePriceUseCase.execute(
                LocalDateTime.parse("2026-01-15T10:00:00"),
                35455L,
                1L
        )).thenReturn(java.util.Optional.of(new Tienda(
                5L,
                1L,
                LocalDateTime.parse("2026-01-01T00:00:00"),
                LocalDateTime.parse("2026-06-30T23:59:59"),
                2,
                35455L,
                1,
                new BigDecimal("30.50"),
                "EUR"
        )));

        mockMvc.perform(get("/tiendas/precio")
                        .param("fechaAplicacion", "2026-01-15T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2026-01-01T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2026-06-30T23:59:59"))
                .andExpect(jsonPath("$.price").value(30.50))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.priority").doesNotExist())
                .andExpect(jsonPath("$.curr").doesNotExist())
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.direccion").doesNotExist());
    }

    @Test
    void deberiaRetornarBadRequestCuandoValidacionFallaEnServicio() throws Exception {
        when(createTiendaUseCase.execute(
                1L,
                LocalDateTime.parse("2026-06-30T23:59:59"),
                LocalDateTime.parse("2026-01-01T00:00:00"),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                "EUR"
        )).thenThrow(new IllegalArgumentException("END_DATE no puede ser anterior a START_DATE"));

        String body = """
                {
                  "brandId": 1,
                  "startDate": "2026-06-30T23:59:59",
                  "endDate": "2026-01-01T00:00:00",
                  "priceList": 1,
                  "productId": 35455,
                  "priority": 0,
                  "price": 35.50,
                  "curr": "EUR"
                }
                """;

        mockMvc.perform(post("/tiendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("END_DATE no puede ser anterior a START_DATE"));
    }
}
