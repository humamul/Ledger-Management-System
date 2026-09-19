package com.skillovilla.application.facade;

import com.skillovilla.application.dto.StockMovementDto;

import java.util.List;

public interface StockMovementFacade {
    StockMovementDto create(StockMovementDto dto);
    List<StockMovementDto> getAll();
    StockMovementDto getById(Long id);
    void delete(Long id);
}
