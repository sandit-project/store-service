package com.example.storeservice.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartItem(
        Integer cartUid,
        @NotBlank(message = "menu must be defined")
        String menuName,
        @NotNull(message = "amount must be defined")
        @Min(value = 1, message = "You must order at least 1 item.")
        int amount,
        @NotNull(message = "price must be defined")
        @Min(value = 1, message = "Price at least 1")
        int price,
        @NotNull(message = "calorie must be defined")
        @Min(value = 1, message = "Calorie at least 1")
        Double calorie
) {
    public static CartItem sample() {
        return new CartItem(
                1,
                "테스트 샌드위치",
                2,      // 수량
                5000,   // 가격
                350.0   // 칼로리
        );
    }
}
