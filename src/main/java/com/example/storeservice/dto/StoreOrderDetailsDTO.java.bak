package com.example.storeservice.dto;

import com.example.storeservice.domain.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Builder(toBuilder = true)
public class StoreOrderDetailsDTO {
    private Integer uid;
    private Integer userUid;
    private Integer storeUid;
    private List<CartItem> items;
    private String payment;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime reservationDate;

    public static StoreOrderDetailsDTO sampleData1() {
        return StoreOrderDetailsDTO.builder()
                .uid(100)
                .userUid(10)
                .storeUid(1)
                .items(List.of(CartItem.sample()))
                .payment("CASH")
                .status("PENDING")
                .createdDate(LocalDateTime.now())
                .reservationDate(LocalDateTime.now().plusHours(1))
                .build();
    }
    public static StoreOrderDetailsDTO sampleData2() {
        return StoreOrderDetailsDTO.builder()
                .uid(200)
                .userUid(15)
                .storeUid(1)
                .items(List.of(CartItem.sample()))
                .payment("CASH")
                .status("PENDING")
                .createdDate(LocalDateTime.now().plusMinutes(5))
                .reservationDate(LocalDateTime.now().plusHours(1))
                .build();
    }

}
