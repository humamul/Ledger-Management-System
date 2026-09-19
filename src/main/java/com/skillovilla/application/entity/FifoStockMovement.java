package com.skillovilla.application.entity;

import com.skillovilla.application.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fifo_stock_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FifoStockMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "in_movement_id", nullable = false)
    private StockMovement inMovement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "out_movement_id", nullable = false)
    private StockMovement outMovement;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "assign_date")
    private LocalDateTime assignDate;

    @Column(name = "is_cancelled", nullable = false)
    @Builder.Default
    private Boolean isCancelled = false;

    public void cancel() {
        this.isCancelled = true;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void init() {
        super.init();
        if (this.isCancelled == null) this.isCancelled = false;
        if (this.assignDate == null) this.assignDate = LocalDateTime.now();
    }
}
