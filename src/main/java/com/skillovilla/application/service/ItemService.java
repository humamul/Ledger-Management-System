package com.skillovilla.application.service;

import com.skillovilla.application.entity.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);
    List<Item> getAll();
    Item getById(Long id);
    Item update(Long id, Item incoming);
    void delete(Long id);
}
