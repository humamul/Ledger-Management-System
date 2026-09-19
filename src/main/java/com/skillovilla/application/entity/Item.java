package com.skillovilla.application.entity;

import com.skillovilla.application.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;

    @Column(name = "is_disabled", nullable = false)
    private Boolean isDisabled;

    public void update(Item other) {
        if (other.getName() != null) this.name = other.getName();
        if (other.getUnit() != null) this.unit = other.getUnit();
        this.setUpdatedAt(LocalDateTime.now());
    }


    public void init() {
        super.init();
        if (this.isDisabled == null) this.isDisabled = false;
    }

}
