package com.manolinho.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Stock {

    private Long productId;
    private String talla;
    private Integer disponibles;
}
