package com.example.storeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerStoreListResponseDTO {

    private String  storeName;
    private String  storeAddress;
    private Double  storeLatitude;
    private Double  storeLongitude;

}
