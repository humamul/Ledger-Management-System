package com.skillovilla.application.service.impl;

import com.skillovilla.application.dto.IssueCostDto;
import com.skillovilla.application.dto.StockReportDto;
import com.skillovilla.application.entity.FifoStockMovement;
import com.skillovilla.application.entity.StockMovement;
import com.skillovilla.application.repository.FifoStockMovementRepository;
import com.skillovilla.application.repository.StockMovementRepository;
import com.skillovilla.application.service.StockReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockReportServiceImpl implements StockReportService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private FifoStockMovementRepository fifoStockMovementRepository;

    @Override
    public StockReportDto getCurrentStock(Long itemId, Long warehouseId) {
        List<StockMovement> unexhaustedInMovements;
        if (warehouseId == null) {
            unexhaustedInMovements = stockMovementRepository.findUnexhaustedInMovementsByItemId(itemId);
        } else {
            unexhaustedInMovements = stockMovementRepository.findUnexhaustedInMovements(itemId, warehouseId);
        }

        int totalQuantity = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        String itemName = "";

        for (StockMovement inMovement : unexhaustedInMovements) {
            itemName = inMovement.getItemName(); // capture name from any record
            Integer allocated = fifoStockMovementRepository.getTotalUtilizedForInMovement(inMovement.getId());
            if (allocated == null) allocated = 0;
            
            int remaining = inMovement.getQuantity() - allocated;
            if (remaining > 0) {
                totalQuantity += remaining;
                if (inMovement.getUnitPrice() != null) {
                    totalValue = totalValue.add(inMovement.getUnitPrice().multiply(new BigDecimal(remaining)));
                }
            }
        }

        return StockReportDto.builder()
                .itemId(itemId)
                .warehouseId(warehouseId)
                .itemName(itemName)
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .build();
    }

    @Override
    public StockReportDto getPastStock(Long itemId, Long warehouseId, LocalDateTime asOfDate) {
        // Find all IN movements that arrived on or before asOfDate
        List<StockMovement> allInMovements = stockMovementRepository.findAllByItemIdAndMovementType(itemId, "IN");
        
        int totalQuantity = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        String itemName = "";

        for (StockMovement inMovement : allInMovements) {
            if (inMovement.getMovementDate().isAfter(asOfDate) || inMovement.getOriginalMovementId() != null) {
                continue; // Skip arrivals that happened in the future or are cancellations
            }
            
            itemName = inMovement.getItemName();
            
            // Find all allocations from this IN movement
            List<FifoStockMovement> allocations = fifoStockMovementRepository.findByInMovementId(inMovement.getId());
            int totalAllocatedBeforeDate = 0;
            
            for (FifoStockMovement fsm : allocations) {
                if (fsm.getAssignDate().isAfter(asOfDate)) {
                    continue; // This allocation happened in the future, ignore it
                }
                
                // If the allocation was cancelled BEFORE or ON asOfDate, it means the stock was returned!
                if (Boolean.TRUE.equals(fsm.getIsCancelled()) && fsm.getCancelledDate() != null && !fsm.getCancelledDate().isAfter(asOfDate)) {
                    continue; // Stock was returned before our date, so it doesn't count as allocated
                }
                
                // Otherwise, the stock was allocated and not yet returned (as of the past date)
                totalAllocatedBeforeDate += fsm.getQuantity();
            }
            
            int remaining = inMovement.getQuantity() - totalAllocatedBeforeDate;
            
            // Filter by warehouse if provided
            if (warehouseId == null || warehouseId.equals(inMovement.getWarehouse().getId())) {
                if (remaining > 0) {
                    totalQuantity += remaining;
                    if (inMovement.getUnitPrice() != null) {
                        totalValue = totalValue.add(inMovement.getUnitPrice().multiply(new BigDecimal(remaining)));
                    }
                }
            }
        }

        return StockReportDto.builder()
                .itemId(itemId)
                .warehouseId(warehouseId)
                .itemName(itemName)
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .build();
    }

    @Override
    public IssueCostDto getIssueCost(Long outMovementId) {
        List<FifoStockMovement> allocations = fifoStockMovementRepository.findByOutMovementId(outMovementId);
        
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQuantity = 0;
        List<IssueCostDto.AllocationDetailDto> details = new ArrayList<>();
        
        for (FifoStockMovement fsm : allocations) {
            BigDecimal allocationCost = BigDecimal.ZERO;
            if (fsm.getInMovement().getUnitPrice() != null) {
                allocationCost = fsm.getInMovement().getUnitPrice().multiply(new BigDecimal(fsm.getQuantity()));
                totalCost = totalCost.add(allocationCost);
            }
            totalQuantity += fsm.getQuantity();
            
            details.add(IssueCostDto.AllocationDetailDto.builder()
                    .inMovementId(fsm.getInMovement().getId())
                    .arrivalDate(fsm.getInMovement().getMovementDate())
                    .quantityTaken(fsm.getQuantity())
                    .unitPrice(fsm.getInMovement().getUnitPrice())
                    .allocationCost(allocationCost)
                    .build());
        }
        
        return IssueCostDto.builder()
                .outMovementId(outMovementId)
                .totalQuantity(totalQuantity)
                .totalCost(totalCost)
                .allocations(details)
                .build();
    }
}
