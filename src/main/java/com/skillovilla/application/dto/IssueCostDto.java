package com.skillovilla.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueCostDto {
    private Long outMovementId;
    private BigDecimal totalCost;
    private Integer totalQuantity;
    private List<AllocationDetailDto> allocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationDetailDto {
        private Long inMovementId;
        private LocalDateTime arrivalDate;
        private Integer quantityTaken;
        private BigDecimal unitPrice;
        private BigDecimal allocationCost;
    }
}
