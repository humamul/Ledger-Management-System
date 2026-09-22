package com.skillovilla.application.service;

import com.skillovilla.application.dto.IssueCostDto;
import com.skillovilla.application.dto.StockReportDto;

import java.time.LocalDateTime;

public interface StockReportService {
    StockReportDto getCurrentStock(Long itemId, Long warehouseId);
    StockReportDto getPastStock(Long itemId, Long warehouseId, LocalDateTime asOfDate);
    IssueCostDto getIssueCost(Long outMovementId);
}
