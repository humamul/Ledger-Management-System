package com.skillovilla.application.facade.impl;

import com.skillovilla.application.assembler.WarehouseDTOAssembler;
import com.skillovilla.application.dto.WarehouseDto;
import com.skillovilla.application.facade.WarehouseFacade;
import com.skillovilla.application.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseFacadeImpl implements WarehouseFacade {

    @Autowired
    private WarehouseDTOAssembler assembler;

    @Autowired
    private WarehouseService service;

    @Override
    public WarehouseDto create(WarehouseDto dto) {
        return assembler.toDto(service.create(assembler.toEntity(dto)));
    }

    @Override
    public List<WarehouseDto> getAll() {
        return service.getAll().stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseDto getById(Long id) {
        return assembler.toDto(service.getById(id));
    }

    @Override
    public WarehouseDto update(Long id, WarehouseDto dto) {
        return assembler.toDto(service.update(id, assembler.toEntity(dto)));
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
