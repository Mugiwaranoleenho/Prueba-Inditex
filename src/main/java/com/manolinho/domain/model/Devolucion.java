package com.manolinho.domain.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class Devolucion {

    private final Long id;
    private final Long pedidoId;
    private final LocalDateTime fechaSolicitud;
    private final List<LineaPedido> lineasDevueltas;
    private final BigDecimal importeReembolso;
    private final String estado;

    public Devolucion(Long id,
                      Long pedidoId,
                      LocalDateTime fechaSolicitud,
                      List<LineaPedido> lineasDevueltas,
                      BigDecimal importeReembolso,
                      String estado) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.fechaSolicitud = fechaSolicitud;
        this.lineasDevueltas = List.copyOf(lineasDevueltas);
        this.importeReembolso = importeReembolso;
        this.estado = estado;
    }
}
