package com.manolinho.infrastructure.repository;

import com.manolinho.domain.model.Tienda;
import com.manolinho.domain.repository.TiendaRepository;
import com.manolinho.infrastructure.config.TiendaJPAEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class TiendaRepositoryAdapter implements TiendaRepository {

    private final SpringDataTiendaRepository jpaRepository;

    public TiendaRepositoryAdapter(SpringDataTiendaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tienda save(Tienda tienda) {
        TiendaJPAEntity entity = new TiendaJPAEntity();
        entity.setBrandId(tienda.getBrandId());
        entity.setStartDate(tienda.getStartDate());
        entity.setEndDate(tienda.getEndDate());
        entity.setPriceList(tienda.getPriceList());
        entity.setProductId(tienda.getProductId());
        entity.setPriority(tienda.getPriority());
        entity.setPrice(tienda.getPrice());
        entity.setCurr(tienda.getCurr());

        TiendaJPAEntity saved = jpaRepository.save(entity);
        return new Tienda(
                saved.getId(),
                saved.getBrandId(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getPriceList(),
                saved.getProductId(),
                saved.getPriority(),
                saved.getPrice(),
                saved.getCurr()
        );
    }

    @Override
    public Optional<Tienda> findApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        return jpaRepository
                .findApplicablePrices(
                        brandId,
                        productId,
                        applicationDate,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .map(saved -> new Tienda(
                        saved.getId(),
                        saved.getBrandId(),
                        saved.getStartDate(),
                        saved.getEndDate(),
                        saved.getPriceList(),
                        saved.getProductId(),
                        saved.getPriority(),
                        saved.getPrice(),
                        saved.getCurr()
                ));
    }
}
