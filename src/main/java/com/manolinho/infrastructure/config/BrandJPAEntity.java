package com.manolinho.infrastructure.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "brands")
public class BrandJPAEntity {

    @Id
    private Long id;

    private String nombre;


}
