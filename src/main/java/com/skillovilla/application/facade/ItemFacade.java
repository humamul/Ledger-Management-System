package com.skillovilla.application.facade;

import com.skillovilla.application.dto.ItemDto;

import java.util.List;

public interface ItemFacade {
    ItemDto create(ItemDto dto);
    List<ItemDto> getAll();
    ItemDto getById(Long id);
    ItemDto update(Long id, ItemDto dto);
    void delete(Long id);
}
