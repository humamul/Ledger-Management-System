package com.skillovilla.application.controller;

import com.skillovilla.application.dto.StockMovementDto;
import com.skillovilla.application.facade.StockMovementFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {

    @Autowired
    private StockMovementFacade facade;

    @PostMapping
    public ResponseEntity<StockMovementDto> create(@RequestBody StockMovementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.create(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<StockMovementDto>> createMovementsInBulk(@RequestBody List<StockMovementDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.createInBulk(dtos));
    }

    @GetMapping
    public ResponseEntity<com.skillovilla.application.dto.PagedResponseDto<StockMovementDto>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        return ResponseEntity.ok(facade.getAll(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<StockMovementDto> cancelMovement(@PathVariable Long id, @RequestParam(required = false) String reason, @RequestParam String recordedBy) {
        return ResponseEntity.ok(facade.cancelMovement(id, reason, recordedBy));
    }
}
