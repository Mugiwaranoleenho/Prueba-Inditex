package com.manolinho.domain.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class Pedido {

    private final Long id;
    private final String numeroPedido;
    private final LocalDateTime fechaCompra;
    private final List<LineaPedido> lineas;
    private final BigDecimal total;
    private final String estado;

    public Pedido(Long id,
                  String numeroPedido,
                  LocalDateTime fechaCompra,
                  List<LineaPedido> lineas,
                  BigDecimal total,
                  String estado) {
        this.id = id;
        this.numeroPedido = numeroPedido;
        this.fechaCompra = fechaCompra;
        this.lineas = List.copyOf(lineas);
        this.total = total;
        this.estado = estado;
    }
}
