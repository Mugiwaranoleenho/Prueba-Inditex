package com.manolinho.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(statements = {
        "DELETE FROM tiendas",
        "MERGE INTO brands (id, nombre) KEY (id) VALUES (1, 'ZARA')",
        "MERGE INTO products (id, nombre) KEY (id) VALUES (35455, 'CAMISETA')",
        "INSERT INTO tiendas (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) " +
                "VALUES (1, TIMESTAMP '2020-06-14 00:00:00', TIMESTAMP '2020-12-31 23:59:59', 1, 35455, 0, 35.50, 'EUR')"
})
class SecurityPricingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void empleadoDeberiaRecibirDescuentoDel5PorCiento() throws Exception {
        mockMvc.perform(get("/tiendas/precio")
                        .with(httpBasic("empleado", "empleado123"))
                        .param("fechaAplicacion", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalPrice").value(35.50))
                .andExpect(jsonPath("$.discountPercent").value(0.05))
                .andExpect(jsonPath("$.finalPrice").value(33.73));
    }

    @Test
    void empleadoJefeDeberiaRecibirDescuentoDel15PorCiento() throws Exception {
        mockMvc.perform(get("/tiendas/precio")
                        .with(httpBasic("empleado_jefe", "jefe123"))
                        .param("fechaAplicacion", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalPrice").value(35.50))
                .andExpect(jsonPath("$.discountPercent").value(0.15))
                .andExpect(jsonPath("$.finalPrice").value(30.18));
    }

    @Test
    void clienteBloqueadoNoDeberiaPoderAcceder() throws Exception {
        mockMvc.perform(get("/tiendas/precio")
                        .with(httpBasic("cliente_bloqueado", "bloqueado123"))
                        .param("fechaAplicacion", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isForbidden());
    }
}
