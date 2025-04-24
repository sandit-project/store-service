package com.example.storeservice.dto;

import lombok.Getter;

@Getter
public class StoreOrderListRequestDTO {
    private int limit;
    private Long lastUid;
}
