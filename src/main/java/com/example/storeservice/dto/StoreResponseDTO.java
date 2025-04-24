package com.example.storeservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
public class StoreResponseDTO {
    private Long    uid;
    private String  storeName;
    private String  storeAddress;
    private String  storePostcode;
    private Double  storeLatitude;
    private Double  storeLongitude;
    private String  storeStatus;
    private LocalDateTime storeCreatedDate;
}