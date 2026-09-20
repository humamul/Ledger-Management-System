package com.skillovilla.application.repository;

import com.skillovilla.application.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    @Query(value = """
    SELECT sm.* 
    FROM stock_movement sm
    LEFT JOIN fifo_stock_movement fsm 
           ON sm.id = fsm.in_movement_id AND fsm.is_cancelled = 0
    WHERE sm.item_id = :itemId 
      AND sm.warehouse_id = :warehouseId 
      AND sm.movement_type = 'IN'
    GROUP BY sm.id
    HAVING sm.quantity > IFNULL(SUM(fsm.quantity), 0)
    ORDER BY sm.movement_date ASC
""", nativeQuery = true)
    List<StockMovement> findUnexhaustedInMovements(@Param("itemId") Long itemId,
                                                   @Param("warehouseId") Long warehouseId);}
