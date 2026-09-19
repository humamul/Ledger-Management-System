package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.Item;
import com.skillovilla.application.repository.ItemRepository;
import com.skillovilla.application.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item create(Item item) {
        if (itemRepository.existsByCode(item.getCode())) {
            throw new RuntimeException("Item with code '" + item.getCode() + "' already exists");
        }
        item.init();
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    @Override
    public Item update(Long id, Item incoming) {
        Item existing = getById(id);
        existing.update(incoming);
        return itemRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        itemRepository.delete(getById(id));
    }
}
