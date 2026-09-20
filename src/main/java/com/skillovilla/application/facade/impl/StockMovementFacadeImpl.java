package com.skillovilla.application.facade.impl;

import com.skillovilla.application.assembler.StockMovementDTOAssembler;
import com.skillovilla.application.dto.StockMovementDto;
import com.skillovilla.application.entity.StockMovement;
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
    @Transactional
    public List<StockMovementDto> createInBulk(List<StockMovementDto> dtos) {
        List<StockMovement> entities = dtos.stream()
                .map(assembler::toEntity)
                .collect(Collectors.toList());
        return service.createInBulk(entities).stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public com.skillovilla.application.dto.PagedResponseDto<StockMovementDto> getAll(int page, int size, String sortBy, String sortOrder) {
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(
                "DESC".equalsIgnoreCase(sortOrder) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC,
                sortBy != null ? sortBy : "id"
        );
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        org.springframework.data.domain.Page<StockMovement> movementPage = service.getAll(pageable);

        List<StockMovementDto> list = movementPage.getContent().stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());

        return com.skillovilla.application.dto.PagedResponseDto.<StockMovementDto>builder()
                .list(list)
                .totalElements(movementPage.getTotalElements())
                .hasNext(movementPage.hasNext())
                .build();
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
