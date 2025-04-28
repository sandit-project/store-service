package com.example.storeservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreUidResponseDTO {
    private Long storeUid;
    private String storeName;
}
