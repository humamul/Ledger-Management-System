package com.skillovilla.application.service;

import com.skillovilla.application.entity.Item;
import com.skillovilla.application.entity.StockMovement;
import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.repository.ItemRepository;
import com.skillovilla.application.repository.StockMovementRepository;
import com.skillovilla.application.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    public StockMovement create(StockMovement stockMovement) {
        // Validate item exists
        Item item = itemRepository.findById(stockMovement.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + stockMovement.getItem().getId()));

        // Validate warehouse exists
        Warehouse warehouse = warehouseRepository.findById(stockMovement.getWarehouse().getId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + stockMovement.getWarehouse().getId()));

        stockMovement.setItem(item);
        stockMovement.setWarehouse(warehouse);
        stockMovement.init();
        return stockMovementRepository.save(stockMovement);
    }

    public List<StockMovement> getAll() {
        return stockMovementRepository.findAll();
    }

    public StockMovement getById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StockMovement not found with id: " + id));
    }

    public void delete(Long id) {
        StockMovement existing = getById(id);
        stockMovementRepository.delete(existing);
    }
}
