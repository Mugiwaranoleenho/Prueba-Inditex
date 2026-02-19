package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateDevolucionUseCase;
import com.manolinho.domain.model.Devolucion;
import com.manolinho.domain.model.LineaPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DevolucionController.class)
@AutoConfigureMockMvc(addFilters = false)
class DevolucionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateDevolucionUseCase createDevolucionUseCase;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deberiaCrearDevolucionConPost() throws Exception {
        Devolucion devolucion = new Devolucion(
                1L,
                10L,
                LocalDateTime.parse("2026-02-20T11:00:00"),
                List.of(new LineaPedido(35455L, "M", 1, new BigDecimal("19.99"))),
                new BigDecimal("19.99"),
                "APROBADA"
        );
        when(createDevolucionUseCase.execute(eq(10L), eq(LocalDateTime.parse("2026-02-20T11:00:00")), any(List.class)))
                .thenReturn(devolucion);

        String body = """
                {
                  "pedidoId": 10,
                  "fechaSolicitud": "2026-02-20T11:00:00",
                  "lineas": [
                    { "productId": 35455, "talla": "M", "unidades": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/devoluciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pedidoId").value(10))
                .andExpect(jsonPath("$.importeReembolso").value(19.99))
                .andExpect(jsonPath("$.estado").value("APROBADA"));
    }
}
