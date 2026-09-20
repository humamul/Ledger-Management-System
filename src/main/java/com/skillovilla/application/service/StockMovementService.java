package com.skillovilla.application.service;

import com.skillovilla.application.entity.StockMovement;

import java.util.List;

public interface StockMovementService {
    StockMovement create(StockMovement stockMovement);
    List<StockMovement> createInBulk(List<StockMovement> stockMovements);
    org.springframework.data.domain.Page<StockMovement> getAll(org.springframework.data.domain.Pageable pageable);
    StockMovement getById(Long id);
    void delete(Long id);
}
