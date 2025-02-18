package com.dogpaws.backend.dto.rim;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderItemDto {
    private Long orderItemId;      // 주문 상품 ID
    private String qlId;           // 주문번호
    private Long productId;        // 상품 ID
    private String productName;    // 상품명
    private int amount;            // 수량
    private int itemPrice;         // 상품 가격
    private List<OrderItemOptionDto> options; // 상품 옵션 목록
}