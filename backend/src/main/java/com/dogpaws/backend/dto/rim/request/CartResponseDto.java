package com.dogpaws.backend.dto.rim.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CartResponseDto {
    private Long cartItemId;
    private String productName;
    private int basePrice;
    private int quantity;
    private List<CartOptionInfo> options;
    private int totalPrice;

    @Builder
    public CartResponseDto(Long cartItemId, String productName, int basePrice, int quantity,List<CartOptionInfo> options, int totalPrice) {
        this.cartItemId = cartItemId;
        this.productName = productName;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.options = options;
        this.totalPrice = totalPrice;
    }

    @Getter
    @NoArgsConstructor
    public static class CartOptionInfo {
        private String optionName;
        private int optionPrice;
        private int quantity;

        @Builder
        public CartOptionInfo(String optionName, int optionPrice, int quantity) {
            this.optionName = optionName;
            this.optionPrice = optionPrice;
            this.quantity = quantity;
        }
    }
}
