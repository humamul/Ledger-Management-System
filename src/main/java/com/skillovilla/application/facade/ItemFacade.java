package com.skillovilla.application.facade;

import com.skillovilla.application.dto.ItemDto;
import com.skillovilla.application.dto.PagedResponseDto;

import java.util.List;

public interface ItemFacade {
    ItemDto create(ItemDto dto);
    PagedResponseDto<ItemDto> getAll(int page, int size, String sortBy, String sortOrder, Boolean isDisabled);
    ItemDto disable(String code);
}
