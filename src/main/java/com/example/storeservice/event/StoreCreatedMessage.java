package com.example.storeservice.event;

import com.example.storeservice.type.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StoreCreatedMessage {
    private String  storeName;
    private Long    managerUid;
    private Long    socialUid;
    private String  storeAddress;
    private String  storePostcode;
    private Double  storeLatitude;
    private Double  storeLongitude;
    private String storeStatus;


}
