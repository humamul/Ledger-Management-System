package com.skillovilla.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDto {

    private Long id;
    private String code;
    private String name;
    private Boolean isDisabled;
}
