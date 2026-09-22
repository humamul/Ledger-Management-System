package com.skillovilla.application.controller;

import com.skillovilla.application.dto.PagedResponseDto;
import com.skillovilla.application.dto.WarehouseDto;
import com.skillovilla.application.facade.WarehouseFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseFacade facade;

    @PostMapping
    public ResponseEntity<WarehouseDto> create(@RequestBody WarehouseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.create(dto));
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<WarehouseDto>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Boolean isDisabled
    ) {
        return ResponseEntity.ok(facade.getAll(page, size, sortBy, sortOrder, isDisabled));
    }

    @PatchMapping("/{code}/disable")
    public ResponseEntity<WarehouseDto> disable(@PathVariable String code) {
        return ResponseEntity.ok(facade.disable(code));
    }
}
