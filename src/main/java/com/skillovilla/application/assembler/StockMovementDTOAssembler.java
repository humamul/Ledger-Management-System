package com.skillovilla.application.assembler;

import com.skillovilla.application.dto.StockMovementDto;
import com.skillovilla.application.entity.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMovementDTOAssembler {

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "item.id", target = "itemId")
    StockMovementDto toDto(StockMovement entity);

    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "itemId", target = "item.id")
    StockMovement toEntity(StockMovementDto dto);
}
