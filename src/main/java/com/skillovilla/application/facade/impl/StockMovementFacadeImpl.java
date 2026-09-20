package com.skillovilla.application.facade.impl;

import com.skillovilla.application.assembler.StockMovementDTOAssembler;
import com.skillovilla.application.dto.StockMovementDto;
import com.skillovilla.application.facade.StockMovementFacade;
import com.skillovilla.application.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockMovementFacadeImpl implements StockMovementFacade {

    @Autowired
    private StockMovementDTOAssembler assembler;

    @Autowired
    private StockMovementService service;

    @Override
    @Transactional
    public StockMovementDto create(StockMovementDto dto) {
        return assembler.toDto(service.create(assembler.toEntity(dto)));
    }

    @Override
    public List<StockMovementDto> getAll() {
        return service.getAll().stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StockMovementDto getById(Long id) {
        return assembler.toDto(service.getById(id));
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
