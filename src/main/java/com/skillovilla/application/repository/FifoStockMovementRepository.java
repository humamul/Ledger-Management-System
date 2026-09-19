package com.skillovilla.application.repository;

import com.skillovilla.application.entity.FifoStockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FifoStockMovementRepository extends JpaRepository<FifoStockMovement, Long> {
}
