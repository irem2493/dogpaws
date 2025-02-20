package com.dogpaws.backend.dto.rim.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponseDto {
    private List<CartListResponseDto> cartItems;
    private int totalProductPrice;    // 총 상품 금액
    private int deliveryFee;         // 배송비
    private int totalOrderPrice;     // 최종 주문 금액
    private int totalQuantity;       // 총 수량
}