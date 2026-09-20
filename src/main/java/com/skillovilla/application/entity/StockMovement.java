package com.skillovilla.application.entity;

import com.skillovilla.application.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "movement_type", nullable = false)
    private String movementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "reference_doc")
    private String referenceDoc;

    @Column(name = "movement_date")
    private LocalDateTime movementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_unit")
    private String itemUnit;

    @Column(name = "reason")
    private String reason;

    @Column(name = "recorded_by")
    private String recordedBy;
    public void init() {
        super.init();
        if (this.movementDate == null) this.movementDate = LocalDateTime.now();
    }

    public void init(Item item, Warehouse warehouse, Warehouse destinationWarehouse) {
        super.init();
        this.item = item;
        this.warehouse = warehouse;
        this.destinationWarehouse = destinationWarehouse;
        if (item != null) {
            this.itemName = item.getName();
            this.itemUnit = item.getUnit();
        }
        if (this.movementDate == null) {
            this.movementDate = LocalDateTime.now();
        }
    }
}
