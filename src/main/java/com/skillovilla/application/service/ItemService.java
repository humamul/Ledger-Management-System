package com.skillovilla.application.service;

import com.skillovilla.application.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    Item create(Item item);
    Page<Item> getAll(Boolean isDisabled, Pageable pageable);
    Item getById(Long id);
    Item update(Long id, Item incoming);
    void delete(Long id);
    Item disable(String code);
    Item getByIdForUpdate(Long id);
}
