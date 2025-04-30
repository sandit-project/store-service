package com.example.storeservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RabbitResponseDTO {
    private boolean isSuccess;
    private String message;
}
