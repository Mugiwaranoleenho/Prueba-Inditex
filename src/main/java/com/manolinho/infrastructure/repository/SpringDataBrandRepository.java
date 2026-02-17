package com.manolinho.infrastructure.repository;

import com.manolinho.infrastructure.config.BrandJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBrandRepository extends JpaRepository<BrandJPAEntity, Long> {
}
