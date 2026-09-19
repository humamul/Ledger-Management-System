package com.skillovilla.application.facade.impl;

import com.skillovilla.application.assembler.WarehouseDTOAssembler;
import com.skillovilla.application.dto.PagedResponseDto;
import com.skillovilla.application.dto.WarehouseDto;
import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.facade.WarehouseFacade;
import com.skillovilla.application.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WarehouseFacadeImpl implements WarehouseFacade {

    private final WarehouseDTOAssembler assembler;
    private final WarehouseService service;

    @Override
    public WarehouseDto create(WarehouseDto dto) {
        return assembler.toDto(service.create(assembler.toEntity(dto)));
    }

    @Override
    public PagedResponseDto<WarehouseDto> getAll(int page, int size, String sortBy, String sortOrder, Boolean isDisabled) {
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "id"
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Warehouse> warehousePage = service.getAll(isDisabled, pageable);

        List<WarehouseDto> list = warehousePage.getContent().stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());

        return PagedResponseDto.<WarehouseDto>builder()
                .list(list)
                .totalElements(warehousePage.getTotalElements())
                .hasNext(warehousePage.hasNext())
                .build();
    }

    @Override
    public WarehouseDto disable(String code) {
        return assembler.toDto(service.disable(code));
    }
}
