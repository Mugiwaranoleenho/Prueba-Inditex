package com.manolinho.infrastructure.repository;

import com.manolinho.infrastructure.config.TiendaJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataTiendaRepository extends JpaRepository<TiendaJPAEntity, Long> {

    Optional<TiendaJPAEntity> findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            Long brandId,
            Long productId,
            LocalDateTime applicationDateForStart,
            LocalDateTime applicationDateForEnd
    );
}
