package com.dogpaws.frontend.dto.rim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private String manufacturer;
    private String imageUrl;
    private int amount;           // 수량
    private int itemPrice;        // 상품 가격
    private List<OrderItemOptionDto> options;  // 상품 옵션 목록
}