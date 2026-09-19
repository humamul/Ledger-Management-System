package com.skillovilla.application.assembler;

import com.skillovilla.application.dto.WarehouseDto;
import com.skillovilla.application.entity.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseDTOAssembler {
    WarehouseDto toDto(Warehouse entity);
    Warehouse toEntity(WarehouseDto dto);
}
