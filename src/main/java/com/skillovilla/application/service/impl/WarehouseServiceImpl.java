package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.repository.WarehouseRepository;
import com.skillovilla.application.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public Warehouse create(Warehouse warehouse) {
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new RuntimeException("Warehouse with code '" + warehouse.getCode() + "' already exists");
        }
        warehouse.init();
        return warehouseRepository.save(warehouse);
    }

    @Override
    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
    }

    @Override
    public Warehouse update(Long id, Warehouse incoming) {
        Warehouse existing = getById(id);
        existing.update(incoming);
        return warehouseRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        warehouseRepository.delete(getById(id));
    }
}
