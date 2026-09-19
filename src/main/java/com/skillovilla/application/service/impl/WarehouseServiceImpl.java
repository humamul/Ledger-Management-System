package com.skillovilla.application.service.impl;

import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.exception.AlreadyExistsException;
import com.skillovilla.application.exception.ResourceNotFoundException;
import com.skillovilla.application.repository.WarehouseRepository;
import com.skillovilla.application.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public Warehouse create(Warehouse warehouse) {
        warehouse.setId(null);
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new AlreadyExistsException("Warehouse with code '" + warehouse.getCode() + "' already exists");
        }
        warehouse.init();
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Page<Warehouse> getAll(Boolean isDisabled, Pageable pageable) {
        if (isDisabled != null) {
            return warehouseRepository.findAllByIsDisabled(isDisabled, pageable);
        }
        return warehouseRepository.findAll(pageable);
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
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

    @Override
    public Warehouse disable(String code) {
        Warehouse warehouse = warehouseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with code: " + code));
        warehouse.setIsDisabled(Boolean.TRUE);
        return warehouseRepository.save(warehouse);
    }
}
