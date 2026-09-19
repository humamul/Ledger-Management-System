package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.Item;
import com.skillovilla.application.entity.StockMovement;
import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.repository.ItemRepository;
import com.skillovilla.application.repository.StockMovementRepository;
import com.skillovilla.application.repository.WarehouseRepository;
import com.skillovilla.application.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public StockMovement create(StockMovement stockMovement) {
        Item item = itemRepository.findById(stockMovement.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + stockMovement.getItem().getId()));

        Warehouse warehouse = warehouseRepository.findById(stockMovement.getWarehouse().getId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + stockMovement.getWarehouse().getId()));

        stockMovement.setItem(item);
        stockMovement.setWarehouse(warehouse);
        stockMovement.init();
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
