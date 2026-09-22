package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.Item;
import com.skillovilla.application.exception.AlreadyExistsException;
import com.skillovilla.application.exception.ResourceNotFoundException;
import com.skillovilla.application.repository.ItemRepository;
import com.skillovilla.application.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item create(Item item) {
        item.setId(null);
        if (itemRepository.existsByCode(item.getCode())) {
            throw new AlreadyExistsException("Item with code '" + item.getCode() + "' already exists");
        }
        item.init();
        return itemRepository.save(item);
    }

    @Override
    public Page<Item> getAll(Boolean isDisabled, Pageable pageable) {
        if (isDisabled != null) {
            return itemRepository.findAllByIsDisabled(isDisabled, pageable);
        }
        return itemRepository.findAll(pageable);
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
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

    @Override
    public Item disable(String code) {
        Item item = itemRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with code: " + code));
        item.setIsDisabled(Boolean.TRUE);
        return itemRepository.save(item);
    }

    @Override
    public Item getByIdForUpdate(Long id) {
        return itemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
}
