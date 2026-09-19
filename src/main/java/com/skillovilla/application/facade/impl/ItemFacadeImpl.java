package com.skillovilla.application.facade.impl;

import com.skillovilla.application.assembler.ItemDTOAssembler;
import com.skillovilla.application.dto.ItemDto;
import com.skillovilla.application.facade.ItemFacade;
import com.skillovilla.application.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemFacadeImpl implements ItemFacade {

    @Autowired
    private ItemDTOAssembler assembler;

    @Autowired
    private ItemService service;

    @Override
    public ItemDto create(ItemDto dto) {
        return assembler.toDto(service.create(assembler.toEntity(dto)));
    }

    @Override
    public List<ItemDto> getAll() {
        return service.getAll().stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        return assembler.toDto(service.getById(id));
    }

    @Override
    public ItemDto update(Long id, ItemDto dto) {
        return assembler.toDto(service.update(id, assembler.toEntity(dto)));
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
