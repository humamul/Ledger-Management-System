package com.skillovilla.application.facade;

import com.skillovilla.application.dto.PagedResponseDto;
import com.skillovilla.application.dto.WarehouseDto;

public interface WarehouseFacade {
    WarehouseDto create(WarehouseDto dto);
    PagedResponseDto<WarehouseDto> getAll(int page, int size, String sortBy, String sortOrder, Boolean isDisabled);
    WarehouseDto disable(String code);
}
