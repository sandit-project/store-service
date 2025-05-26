package com.example.storeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
public class
StoreRequestDTO {

    @NotBlank(message = "지점명을 입력해주세요.")
    private String storeName;

    @NotNull(message = "매니저 uid를 입력해주세요.")
    private Long userUid;

    @NotBlank(message = "주소를 입력해주세요.")
    private String storeAddress;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String storePostcode;

    @NotNull(message = "위도가 비었습니다.")
    private Double storeLatitude;

    @NotNull(message = "경도가 비었습니다.")
    private Double storeLongitude;

    @NotBlank(message = "상태값을 입력해주세요.")
    private String storeStatus;
}

