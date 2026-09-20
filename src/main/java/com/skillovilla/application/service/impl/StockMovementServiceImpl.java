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
import org.springframework.transaction.annotation.Transactional;

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
    @org.springframework.transaction.annotation.Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public StockMovement create(StockMovement stockMovement) {
        stockMovement.setId(null);
        
        Item item = itemService.getById(stockMovement.getItem().getId());
        if (Boolean.TRUE.equals(item.getIsDisabled())) {
            throw new ResourceNotFoundException("Cannot record movement for disabled itemId: " + item.getId());
        }

        Warehouse warehouse = warehouseService.getById(stockMovement.getWarehouse().getId());
        if (Boolean.TRUE.equals(warehouse.getIsDisabled())) {
            throw new ResourceNotFoundException("Cannot record movement for disabled warehouseId: "+warehouse.getId());
        }
        Warehouse destinationWarehouse = null;
        if (stockMovement.getDestinationWarehouse() != null && stockMovement.getDestinationWarehouse().getId() != null) {
             destinationWarehouse = warehouseService.getById(stockMovement.getDestinationWarehouse().getId());
        }

        if(Objects.equals(stockMovement.getMovementType(), SecurityConstant.IN_MOVEMENT)){
           return saveInMovementStock(stockMovement,item,warehouse,destinationWarehouse);
        }
        else if(Objects.equals(stockMovement.getMovementType(),SecurityConstant.OUT_MOVEMENT)){
            itemService.getByIdForUpdate(item.getId());
            return saveOutMovementStock(stockMovement,item,warehouse,destinationWarehouse);
        }
        else if(Objects.equals(stockMovement.getMovementType(),SecurityConstant.TRANSFER_MOVEMENT)){
            itemService.getByIdForUpdate(item.getId());
            return saveTransferMovementStock(stockMovement,item,warehouse,destinationWarehouse);
        } else {
            throw new RuntimeException("Give proper Value of Movement Type");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockMovement> createInBulk(List<StockMovement> stockMovements) {
        return stockMovements.stream()
                .map(this::create)
                .collect(java.util.stream.Collectors.toList());
    }

    private StockMovement saveOutMovementStock(StockMovement stockMovement, Item item, Warehouse warehouse, Warehouse destinationWarehouse) {

            int requestedQty = stockMovement.getQuantity();

            stockMovement.init(item, warehouse, destinationWarehouse);
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

    private StockMovement saveInMovementStock(StockMovement stockMovement, Item item, Warehouse warehouse,Warehouse destinationWarehouse) {
        stockMovement.init(item, warehouse, destinationWarehouse);
        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public org.springframework.data.domain.Page<StockMovement> getAll(org.springframework.data.domain.Pageable pageable) {
        return stockMovementRepository.findAll(pageable);
    }
    private StockMovement saveTransferMovementStock(StockMovement stockMovement, Item item, Warehouse sourceWarehouse, Warehouse destinationWarehouse) {
        if (destinationWarehouse == null) {
            throw new RuntimeException(" Destination warehouse is required for transfer");
        }

        if (Boolean.TRUE.equals(destinationWarehouse.getIsDisabled())) {
            throw new ResourceNotFoundException("Cannot transfer to disabled warehouseId: " + destinationWarehouse.getId());
        }

        if (Objects.equals(sourceWarehouse.getId(), destinationWarehouse.getId())) {
            throw new IllegalArgumentException("Source and Destination warehouse cannot be the same");
        }

        int transferQty = Math.abs(stockMovement.getQuantity());

        StockMovement outMovement = StockMovement.builder()
                .item(item)
                .warehouse(sourceWarehouse)
                .destinationWarehouse(destinationWarehouse)
                .movementType(SecurityConstant.OUT_MOVEMENT)
                .quantity(transferQty)
                .movementDate(stockMovement.getMovementDate() != null ? stockMovement.getMovementDate() : LocalDateTime.now())
                .referenceDoc(stockMovement.getReferenceDoc())
                .reason(stockMovement.getReason())
                .recordedBy(stockMovement.getRecordedBy())
                .build();

        StockMovement savedOut = saveOutMovementStock(outMovement, item, sourceWarehouse, destinationWarehouse);

        StockMovement inMovement = StockMovement.builder()
                .item(item)
                .warehouse(destinationWarehouse)
                .destinationWarehouse(sourceWarehouse)
                .movementType(SecurityConstant.IN_MOVEMENT)
                .quantity(transferQty)
                .unitPrice(stockMovement.getUnitPrice())
                .movementDate(stockMovement.getMovementDate() != null ? stockMovement.getMovementDate() : LocalDateTime.now())
                .referenceDoc(stockMovement.getReferenceDoc())
                .reason(stockMovement.getReason())
                .recordedBy(stockMovement.getRecordedBy())
                .build();

        saveInMovementStock(inMovement, item, destinationWarehouse, sourceWarehouse);

        return savedOut;
    }

    @Override
    public StockMovement getById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StockMovement not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        throw new RuntimeException("Movements cannot be deleted. They must be cancelled.");
    }

    @Override
    public StockMovement cancelMovement(Long id, String reason, String recordedBy) {
        StockMovement original = getById(id);

        if (stockMovementRepository.existsByOriginalMovementId(id)) {
            throw new RuntimeException("Movement is already cancelled");
        }
        if (original.getOriginalMovementId() != null) {
            throw new RuntimeException("Cannot cancel a cancellation movement");
        }

        StockMovement cancellation = StockMovement.builder()
                .item(original.getItem())
                .warehouse(original.getWarehouse())
                .destinationWarehouse(original.getDestinationWarehouse())
                .movementType(SecurityConstant.OUT_MOVEMENT.equals(original.getMovementType())
                        ? SecurityConstant.IN_MOVEMENT 
                        : SecurityConstant.OUT_MOVEMENT)
                .quantity(Math.abs(original.getQuantity()))
                .unitPrice(original.getUnitPrice())
                .referenceDoc(original.getReferenceDoc())
                .reason("CANCELLATION of ID " + original.getId() + (reason != null ? ": " + reason : ""))
                .recordedBy(recordedBy)
                .movementDate(LocalDateTime.now())
                .originalMovementId(original.getId())
                .itemName(original.getItemName())
                .itemUnit(original.getItemUnit())
                .build();

        cancellation = stockMovementRepository.save(cancellation);

        if (SecurityConstant.OUT_MOVEMENT.equals(original.getMovementType())) {
            List<FifoStockMovement> allocations = fifoStockMovementService.findByOutMovementId(original.getId());
            for (FifoStockMovement allocation : allocations) {
                allocation.cancel(cancellation.getMovementDate());
            }
            fifoStockMovementService.saveAll(allocations);
        }

        return cancellation;
    }
}
