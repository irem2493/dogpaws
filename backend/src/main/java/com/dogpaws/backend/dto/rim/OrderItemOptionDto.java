package com.dogpaws.backend.dto.rim;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

// 주문 상품 옵션 DTO
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemOptionDto {
    private Long optionId;
    private Long orderItemId;
    private String optionName;
    private int optionPrice;
    private int quantity;
}