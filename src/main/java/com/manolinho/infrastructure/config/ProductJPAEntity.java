package com.manolinho.infrastructure.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class ProductJPAEntity {

    @Id
    private Long id;

    private String nombre;


}
