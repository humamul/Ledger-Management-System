package com.skillovilla.application.controller;

import com.skillovilla.application.dto.IssueCostDto;
import com.skillovilla.application.dto.StockReportDto;
import com.skillovilla.application.service.StockReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
public class StockReportController {

    @Autowired
    private StockReportService stockReportService;

    @GetMapping("/stock/current")
    public ResponseEntity<StockReportDto> getCurrentStock(
            @RequestParam Long itemId,
            @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(stockReportService.getCurrentStock(itemId, warehouseId));
    }

    @GetMapping("/stock/past")
    public ResponseEntity<StockReportDto> getPastStock(
            @RequestParam Long itemId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime asOfDate) {
        return ResponseEntity.ok(stockReportService.getPastStock(itemId, warehouseId, asOfDate));
    }

    @GetMapping("/issue-cost/{outMovementId}")
    public ResponseEntity<IssueCostDto> getIssueCost(@PathVariable Long outMovementId) {
        return ResponseEntity.ok(stockReportService.getIssueCost(outMovementId));
    }
}
