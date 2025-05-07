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
    private Integer riderUserUid;
    private Integer riderSocialUid;
    private String addressStart;
    private String addressDestination;
    private LocalDateTime deliveryAcceptTime;
    private LocalDateTime deliveredTime;
}
