package com.skillovilla.application.repository;

import com.skillovilla.application.entity.FifoStockMovement;
import com.skillovilla.application.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FifoStockMovementRepository extends JpaRepository<FifoStockMovement, Long> {
        @Query("SELECT COALESCE(SUM(f.quantity), 0) FROM FifoStockMovement f " +
            "WHERE f.inMovement.id = :inMovementId AND f.isCancelled = false")
    Integer getTotalUtilizedForInMovement(@Param("inMovementId") Long inMovementId);

}
