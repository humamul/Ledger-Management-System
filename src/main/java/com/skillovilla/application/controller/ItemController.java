package com.skillovilla.application.controller;

import com.skillovilla.application.dto.ItemDto;
import com.skillovilla.application.dto.PagedResponseDto;
import com.skillovilla.application.facade.ItemFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemFacade facade;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.create(dto));
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<ItemDto>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Boolean isDisabled
    ) {
        return ResponseEntity.ok(facade.getAll(page, size, sortBy, sortOrder, isDisabled));
    }

    @PatchMapping("/{code}/disable")
    public ResponseEntity<ItemDto> disable(@PathVariable String code) {
        return ResponseEntity.ok(facade.disable(code));
    }
}
