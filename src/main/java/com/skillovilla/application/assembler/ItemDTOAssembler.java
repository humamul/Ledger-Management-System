package com.skillovilla.application.assembler;

import com.skillovilla.application.dto.ItemDto;
import com.skillovilla.application.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemDTOAssembler {
    ItemDto toDto(Item entity);
    Item toEntity(ItemDto itemDto);
}
