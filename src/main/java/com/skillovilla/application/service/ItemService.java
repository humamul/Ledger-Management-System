package com.skillovilla.application.service;

import com.skillovilla.application.entity.Item;
import com.skillovilla.application.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item create(Item item) {
        if (itemRepository.existsByCode(item.getCode())) {
            throw new RuntimeException("Item with code '" + item.getCode() + "' already exists");
        }
        item.init();
        return itemRepository.save(item);
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    public Item update(Long id, Item incoming) {
        Item existing = getById(id);
        existing.update(incoming);
        return itemRepository.save(existing);
    }

    public void delete(Long id) {
        itemRepository.delete(getById(id));
    }
}
