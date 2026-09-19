package com.skillovilla.application.service;

import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public Warehouse create(Warehouse warehouse) {
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new RuntimeException("Warehouse with code '" + warehouse.getCode() + "' already exists");
        }
        warehouse.init();
        return warehouseRepository.save(warehouse);
    }

    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
    }

    public Warehouse update(Long id, Warehouse incoming) {
        Warehouse existing = getById(id);
        existing.update(incoming);
        return warehouseRepository.save(existing);
    }

    public void delete(Long id) {
        Warehouse existing = getById(id);
        warehouseRepository.delete(existing);
    }
}
