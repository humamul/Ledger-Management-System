package com.skillovilla.application.facade;

import com.skillovilla.application.dto.WarehouseDto;

import java.util.List;

public interface WarehouseFacade {
    WarehouseDto create(WarehouseDto dto);
    List<WarehouseDto> getAll();
    WarehouseDto getById(Long id);
    WarehouseDto update(Long id, WarehouseDto dto);
    void delete(Long id);
}
