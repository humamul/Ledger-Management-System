package com.skillovilla.application.service;

import com.skillovilla.application.entity.StockMovement;

import java.util.List;

public interface StockMovementService {
    StockMovement create(StockMovement stockMovement);
    List<StockMovement> getAll();
    StockMovement getById(Long id);
    void delete(Long id);
}
