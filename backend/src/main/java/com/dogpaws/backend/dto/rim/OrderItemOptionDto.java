package com.dogpaws.backend.dto.rim;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 주문 상품 옵션 DTO
@Getter
@Setter
@Builder
public class OrderItemOptionDto {
    private Long optionId;
    private Long orderItemId;
    private String optionName;
    private int optionPrice;
}