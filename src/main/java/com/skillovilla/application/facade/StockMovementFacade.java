package com.skillovilla.application.facade;

import com.skillovilla.application.dto.StockMovementDto;

import java.util.List;

public interface StockMovementFacade {
    StockMovementDto create(StockMovementDto dto);
    List<StockMovementDto> createInBulk(List<StockMovementDto> dtos);
    com.skillovilla.application.dto.PagedResponseDto<StockMovementDto> getAll(int page, int size, String sortBy, String sortOrder);
    StockMovementDto getById(Long id);
    void delete(Long id);
    StockMovementDto cancelMovement(Long id, String reason, String recordedBy);
}
