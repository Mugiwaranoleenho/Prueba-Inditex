package com.manolinho.infrastructure.controller;

import com.manolinho.application.usecase.CreateCambioTallaUseCase;
import com.manolinho.domain.model.CambioTalla;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambioTallaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CambioTallaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateCambioTallaUseCase createCambioTallaUseCase;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void deberiaCrearCambioTallaConPost() throws Exception {
        CambioTalla cambio = new CambioTalla(
                1L,
                10L,
                35455L,
                "M",
                "L",
                1,
                LocalDateTime.parse("2026-02-20T12:00:00"),
                "COMPLETADO"
        );
        when(createCambioTallaUseCase.execute(
                10L,
                35455L,
                "M",
                "L",
                1,
                LocalDateTime.parse("2026-02-20T12:00:00")
        )).thenReturn(cambio);

        String body = """
                {
                  "pedidoId": 10,
                  "productId": 35455,
                  "tallaOrigen": "M",
                  "tallaDestino": "L",
                  "unidades": 1,
                  "fechaSolicitud": "2026-02-20T12:00:00"
                }
                """;

        mockMvc.perform(post("/cambios-talla")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pedidoId").value(10))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.tallaOrigen").value("M"))
                .andExpect(jsonPath("$.tallaDestino").value("L"))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }
}
