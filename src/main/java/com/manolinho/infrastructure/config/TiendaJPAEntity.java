package com.manolinho.infrastructure.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "tiendas")
public class TiendaJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "BRAND_ID")
    private Long brandId;

    @ManyToOne
    @JoinColumn(name = "BRAND_ID", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_TIENDA_BRAND"))
    private BrandJPAEntity brand;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "PRICE_LIST")
    private Integer priceList;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_TIENDA_PRODUCT"))
    private ProductJPAEntity product;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "PRICE", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "CURR", length = 3)
    private String curr;


}
