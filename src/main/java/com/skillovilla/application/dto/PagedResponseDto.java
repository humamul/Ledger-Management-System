package com.skillovilla.application.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponseDto<T> {

    private List<T> list;
    private long totalElements;
    private boolean hasNext;
}
