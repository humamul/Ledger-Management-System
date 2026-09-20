package com.skillovilla.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDto {

    private Long id;
    private Long warehouseId;
    private Long itemId;
    private String movementType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String referenceDoc;
    private LocalDateTime movementDate;

    // Newly added fields
    private Long destinationWarehouseId;
    private String itemName;
    private String itemUnit;
    private String reason;
    private String recordedBy;
}
