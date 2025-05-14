package com.example.storeservice.dto;

import com.example.storeservice.type.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
public class StoreResponseDTO {
    private Long    storeUid;
    private String  storeName;
    private Long    managerUid;
    private Long    socialUid;
    private String  storeAddress;
    private String  storePostcode;
    private Double  storeLatitude;
    private Double  storeLongitude;
    private String storeStatus;
    private LocalDateTime storeCreatedDate;
    private String message;
}