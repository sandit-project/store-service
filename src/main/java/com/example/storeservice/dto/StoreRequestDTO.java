package com.example.storeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequestDTO {

    @NotBlank(message = "지점명을 입력해주세요.")
    private String storeName;

    @NotBlank(message = "주소를 입력해주세요.")
    private String storeAddress;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String storePostcode;

    private Double storeLatitude;

    private Double storeLongitude;

    @NotBlank(message = "상태값을 입력해주세요.")
    private String storeStatus;
}

