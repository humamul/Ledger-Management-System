package com.skillovilla.application.repository;

import com.skillovilla.application.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByCode(String code);
    Optional<Item> findByCode(String code);
    Page<Item> findAllByIsDisabled(Boolean isDisabled, Pageable pageable);
}
