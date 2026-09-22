package com.skillovilla.application.repository;

import com.skillovilla.application.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByCode(String code);
    Optional<Warehouse> findByCode(String code);
    Page<Warehouse> findAllByIsDisabled(Boolean isDisabled, Pageable pageable);
}
