package com.manolinho.infrastructure.persistence.repository;

import com.manolinho.infrastructure.persistence.entity.ProductJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductJPAEntity, Long> {
}
