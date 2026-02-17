package com.manolinho.infrastructure.persistence.repository;

import com.manolinho.infrastructure.persistence.entity.BrandJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBrandRepository extends JpaRepository<BrandJPAEntity, Long> {
}
