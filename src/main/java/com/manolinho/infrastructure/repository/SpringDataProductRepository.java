package com.manolinho.infrastructure.repository;

import com.manolinho.infrastructure.config.ProductJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductJPAEntity, Long> {
}
