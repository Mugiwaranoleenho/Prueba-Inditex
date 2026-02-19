package com.manolinho.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(statements = {
        "DELETE FROM tiendas",
        "MERGE INTO brands (id, nombre) KEY (id) VALUES (1, 'ZARA')",
        "MERGE INTO products (id, nombre) KEY (id) VALUES (35455, 'CAMISETA')",
        "INSERT INTO tiendas (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) " +
                "VALUES (1, TIMESTAMP '2020-06-14 00:00:00', TIMESTAMP '2020-12-31 23:59:59', 1, 35455, 0, 35.50, 'EUR')",
        "INSERT INTO tiendas (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) " +
                "VALUES (1, TIMESTAMP '2020-06-14 15:00:00', TIMESTAMP '2020-06-14 18:30:00', 2, 35455, 1, 25.45, 'EUR')",
        "INSERT INTO tiendas (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) " +
                "VALUES (1, TIMESTAMP '2020-06-15 00:00:00', TIMESTAMP '2020-06-15 11:00:00', 3, 35455, 1, 30.50, 'EUR')",
        "INSERT INTO tiendas (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) " +
                "VALUES (1, TIMESTAMP '2020-06-15 16:00:00', TIMESTAMP '2020-12-31 23:59:59', 4, 35455, 1, 38.95, 'EUR')"
})
class TiendaPrecioEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_peticionALas10DelDia14_producto35455_brand1() throws Exception {
        assertApplicablePrice("2020-06-14T10:00:00", 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59", 35.50);
    }

    @Test
    void test2_peticionALas16DelDia14_producto35455_brand1() throws Exception {
        assertApplicablePrice("2020-06-14T16:00:00", 2, "2020-06-14T15:00:00", "2020-06-14T18:30:00", 25.45);
    }

    @Test
    void test3_peticionALas21DelDia14_producto35455_brand1() throws Exception {
        assertApplicablePrice("2020-06-14T21:00:00", 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59", 35.50);
    }

    @Test
    void test4_peticionALas10DelDia15_producto35455_brand1() throws Exception {
        assertApplicablePrice("2020-06-15T10:00:00", 3, "2020-06-15T00:00:00", "2020-06-15T11:00:00", 30.50);
    }

    @Test
    void test5_peticionALas21DelDia16_producto35455_brand1() throws Exception {
        assertApplicablePrice("2020-06-16T21:00:00", 4, "2020-06-15T16:00:00", "2020-12-31T23:59:59", 38.95);
    }

    private void assertApplicablePrice(String fechaAplicacion,
                                       int expectedPriceList,
                                       String expectedStartDate,
                                       String expectedEndDate,
                                       double expectedPrice) throws Exception {
        mockMvc.perform(get("/tiendas/precio")
                        .with(httpBasic("cliente", "cliente123"))
                        .param("fechaAplicacion", fechaAplicacion)
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                .andExpect(jsonPath("$.originalPrice").value(expectedPrice))
                .andExpect(jsonPath("$.discountPercent").value(0))
                .andExpect(jsonPath("$.finalPrice").value(expectedPrice));
    }
}
