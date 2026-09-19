package com.skillovilla.application.service;

import com.skillovilla.application.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    Warehouse create(Warehouse warehouse);
    Page<Warehouse> getAll(Boolean isDisabled, Pageable pageable);
    Warehouse getById(Long id);
    Warehouse update(Long id, Warehouse incoming);
    void delete(Long id);
    Warehouse disable(String code);
}
