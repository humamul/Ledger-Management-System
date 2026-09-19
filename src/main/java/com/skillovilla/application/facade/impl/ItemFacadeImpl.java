package com.skillovilla.application.facade.impl;

import com.skillovilla.application.assembler.ItemDTOAssembler;
import com.skillovilla.application.dto.ItemDto;
import com.skillovilla.application.dto.PagedResponseDto;
import com.skillovilla.application.entity.Item;
import com.skillovilla.application.facade.ItemFacade;
import com.skillovilla.application.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PagedResponseDto<ItemDto> getAll(int page, int size, String sortBy, String sortOrder, Boolean isDisabled) {
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "id"
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Item> itemPage = service.getAll(isDisabled, pageable);

        List<ItemDto> list = itemPage.getContent().stream()
                .map(assembler::toDto)
                .collect(Collectors.toList());

        return PagedResponseDto.<ItemDto>builder()
                .list(list)
                .totalElements(itemPage.getTotalElements())
                .hasNext(itemPage.hasNext())
                .build();
    }

    @Override
    public ItemDto disable(String code) {
        return assembler.toDto(service.disable(code));
    }
}
