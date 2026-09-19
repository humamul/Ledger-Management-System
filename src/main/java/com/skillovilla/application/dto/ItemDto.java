package com.skillovilla.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id;
    private String code;
    private String name;
    private String unit;
    private Boolean isDisabled;
}
