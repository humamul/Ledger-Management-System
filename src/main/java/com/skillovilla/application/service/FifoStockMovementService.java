package com.skillovilla.application.service;

import com.skillovilla.application.entity.FifoStockMovement;

import java.util.List;

public interface FifoStockMovementService {
    FifoStockMovement create(FifoStockMovement fifoStockMovement);

    void saveAll(List<FifoStockMovement> allocations);

    int getTotalAllocatedForInMovement(Long inMovementId);
}
