package com.manolinho.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class CambioTalla {

    private final Long id;
    private final Long pedidoId;
    private final Long productId;
    private final String tallaOrigen;
    private final String tallaDestino;
    private final Integer unidades;
    private final LocalDateTime fechaSolicitud;
    private final String estado;
}
