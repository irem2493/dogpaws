package com.dogpaws.backend.dto.rim.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartListResponseDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int productPrice;
    private String imageUrl;
    private List<CartOptionResponseDto> cartOptions;

    // 총 금액 계산 메서드
    public int getTotalPrice() {
        return cartOptions.stream()
                .mapToInt(option -> (productPrice + option.getOptionPrice()) * option.getQuantity())
                .sum();
    }

    // 총 수량 계산 메서드
    public int getTotalQuantity() {
        return cartOptions.stream()
                .mapToInt(CartOptionResponseDto::getQuantity)
                .sum();
    }
}