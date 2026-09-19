package com.skillovilla.application.controller;

import com.skillovilla.application.dto.StockMovementDto;
import com.skillovilla.application.facade.StockMovementFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementFacade facade;

    @PostMapping
    public ResponseEntity<StockMovementDto> create(@RequestBody StockMovementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<StockMovementDto>> getAll() {
        return ResponseEntity.ok(facade.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
