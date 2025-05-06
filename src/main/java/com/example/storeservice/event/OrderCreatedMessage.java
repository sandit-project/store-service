package com.example.storeservice.event;

import com.example.storeservice.type.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderCreatedMessage {
    private String merchantUid;
    private OrderStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime reservationDate;
}
