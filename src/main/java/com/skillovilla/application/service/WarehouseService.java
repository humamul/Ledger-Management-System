package com.skillovilla.application.service;

import com.skillovilla.application.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    Warehouse create(Warehouse warehouse);
    List<Warehouse> getAll();
    Warehouse getById(Long id);
    Warehouse update(Long id, Warehouse incoming);
    void delete(Long id);
}
