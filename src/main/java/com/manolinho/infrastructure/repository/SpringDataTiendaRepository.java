package com.manolinho.infrastructure.repository;

import com.manolinho.infrastructure.config.TiendaJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataTiendaRepository extends JpaRepository<TiendaJPAEntity, Long> {

    @Query("""
            SELECT t
            FROM TiendaJPAEntity t
            WHERE t.brandId = :brandId
              AND t.productId = :productId
              AND :applicationDate BETWEEN t.startDate AND t.endDate
            ORDER BY t.priority DESC
            """)
    List<TiendaJPAEntity> findApplicablePrices(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("applicationDate") LocalDateTime applicationDate,
            Pageable pageable
    );
}
