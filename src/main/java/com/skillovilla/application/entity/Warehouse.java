package com.skillovilla.application.entity;

import com.skillovilla.application.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "is_disabled")
    private Boolean isDisabled;

    public void update(Warehouse other) {
        if (other.getCode() != null) this.code = other.getCode();
        if (other.getName() != null) this.name = other.getName();
        if (other.getIsDisabled() != null) this.isDisabled = other.getIsDisabled();
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void init() {
        super.init();
        if (this.isDisabled == null) this.isDisabled = false;
    }

}
