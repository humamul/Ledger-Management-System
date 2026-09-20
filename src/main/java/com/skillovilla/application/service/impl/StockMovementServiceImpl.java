package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.FifoStockMovement;
import com.skillovilla.application.entity.Item;
import com.skillovilla.application.entity.StockMovement;
import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.exception.ResourceNotFoundException;
import com.skillovilla.application.repository.StockMovementRepository;
import com.skillovilla.application.service.FifoStockMovementService;
import com.skillovilla.application.service.ItemService;
import com.skillovilla.application.service.StockMovementService;
import com.skillovilla.application.service.WarehouseService;
import com.skillovilla.application.utility.SecurityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private FifoStockMovementService fifoStockMovementService;

    @Override
    public StockMovement create(StockMovement stockMovement) {
        Item item = itemService.getById(stockMovement.getItem().getId());
        if (Boolean.TRUE.equals(item.getIsDisabled())) {
            throw new ResourceNotFoundException("Cannot record movement for disabled itemId: " + item.getId());
        }

        Warehouse warehouse = warehouseService.getById(stockMovement.getWarehouse().getId());
        if (Boolean.TRUE.equals(warehouse.getIsDisabled())) {
            throw new ResourceNotFoundException("Cannot record movement for disabled warehouseId: "+warehouse.getId());
        }

        if(Objects.equals(stockMovement.getMovementType(), SecurityConstant.IN_MOVEMENT)){
           return saveInMovementStock(stockMovement,item,warehouse);
        }
        else if(Objects.equals(stockMovement.getMovementType(),SecurityConstant.OUT_MOVEMENT)){

            return saveOutMovementStock(stockMovement,item,warehouse);
        }
        stockMovement.init(item,warehouse);
        return null;
    }

    private StockMovement saveOutMovementStock(StockMovement stockMovement, Item item, Warehouse warehouse) {

            int requestedQty = stockMovement.getQuantity();

            stockMovement.init(item, warehouse);
            stockMovement.setQuantity(requestedQty);
            stockMovement.setUnitPrice(null);

            StockMovement savedOut = stockMovementRepository.save(stockMovement);

            List<StockMovement> unexhaustedInMovements = stockMovementRepository
                    .findUnexhaustedInMovements(item.getId(), warehouse.getId());

            int need = requestedQty;
            List<FifoStockMovement> allocations = new ArrayList<>();

            for (StockMovement inMovement : unexhaustedInMovements) {
                if (need <= 0) break;

                int alreadyAllocated = fifoStockMovementService.getTotalAllocatedForInMovement(inMovement.getId());
                int availableInThisMovement = inMovement.getQuantity() - alreadyAllocated;

                if (availableInThisMovement <= 0) continue;

                int take = Math.min(availableInThisMovement, need);

                allocations.add(FifoStockMovement.builder()
                        .inMovement(inMovement)
                        .outMovement(savedOut)
                        .quantity(take)
                        .assignDate(LocalDateTime.now())
                        .isCancelled(false)
                        .build());

                need -= take;
            }

            if (need > 0) {
                throw new ResourceNotFoundException("Insufficient stock! Available stock cannot satisfy requested quantity: " + requestedQty);
            }

            fifoStockMovementService.saveAll(allocations);

            return savedOut;
        }

    private StockMovement saveInMovementStock(StockMovement stockMovement, Item item, Warehouse warehouse) {
        stockMovement.init(item,warehouse);
        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public List<StockMovement> getAll() {
        return stockMovementRepository.findAll();
    }

    @Override
    public StockMovement getById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StockMovement not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        stockMovementRepository.delete(getById(id));
    }
}
