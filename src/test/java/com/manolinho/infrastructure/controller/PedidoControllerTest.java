package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreatePedidoUseCase;
import com.manolinho.application.usecase.CreatePedidoUseCase.CreatePedidoLineaInput;
import com.manolinho.domain.model.LineaPedido;
import com.manolinho.domain.model.Pedido;
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

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreatePedidoUseCase createPedidoUseCase;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deberiaCrearPedidoConPost() throws Exception {
        Pedido pedido = new Pedido(
                1L,
                "PED-001",
                LocalDateTime.parse("2026-02-18T10:00:00"),
                List.of(
                        new LineaPedido(35455L, "M", 2, new BigDecimal("19.99")),
                        new LineaPedido(35456L, "L", 1, new BigDecimal("29.99"))
                ),
                new BigDecimal("69.97"),
                "COMPLETADO"
        );

        when(createPedidoUseCase.execute(eq(LocalDateTime.parse("2026-02-18T10:00:00")), any(List.class)))
                .thenReturn(pedido);

        String body = """
                {
                  "fechaCompra": "2026-02-18T10:00:00",
                  "lineas": [
                    { "productId": 35455, "talla": "M", "unidades": 2, "precioUnitario": 19.99 },
                    { "productId": 35456, "talla": "L", "unidades": 1, "precioUnitario": 29.99 }
                  ]
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPedido").value("PED-001"))
                .andExpect(jsonPath("$.fechaCompra").value("2026-02-18T10:00:00"))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"))
                .andExpect(jsonPath("$.total").value(69.97))
                .andExpect(jsonPath("$.lineas[0].subtotal").value(39.98))
                .andExpect(jsonPath("$.lineas[1].subtotal").value(29.99));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deberiaRetornarBadRequestCuandoValidacionFalla() throws Exception {
        when(createPedidoUseCase.execute(eq(LocalDateTime.parse("2026-02-18T10:00:00")), any(List.class)))
                .thenThrow(new IllegalArgumentException("Debe existir al menos una linea de pedido"));

        String body = """
                {
                  "fechaCompra": "2026-02-18T10:00:00",
                  "lineas": []
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Debe existir al menos una linea de pedido"));
    }
}
