package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.FifoStockMovement;
import com.skillovilla.application.entity.StockMovement;
import com.skillovilla.application.repository.FifoStockMovementRepository;
import com.skillovilla.application.service.FifoStockMovementService;
import com.skillovilla.application.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FifoStockMovementServiceImpl implements FifoStockMovementService {

    @Autowired
    private FifoStockMovementRepository fifoStockMovementRepository;

//    @Autowired
//    private StockMovementService stockMovementService;

    @Override
    public FifoStockMovement create(FifoStockMovement fifoStockMovement) {
        fifoStockMovement.setId(null);

//        StockMovement inMovement = stockMovementService.getById(fifoStockMovement.getInMovement().getId());
//        StockMovement outMovement = stockMovementService.getById(fifoStockMovement.getOutMovement().getId());
//
//        fifoStockMovement.setInMovement(inMovement);
//        fifoStockMovement.setOutMovement(outMovement);
        
        fifoStockMovement.init();

        return fifoStockMovementRepository.save(fifoStockMovement);
    }

    @Override
    public void saveAll(List<FifoStockMovement> allocations) {
        fifoStockMovementRepository.saveAll(allocations);
    }

    @Override
    public int getTotalAllocatedForInMovement(Long inMovementId) {
        return fifoStockMovementRepository.getTotalUtilizedForInMovement(inMovementId);
    }
}
