package com.dogpaws.frontend.dto.rim;

import lombok.*;


@Getter
@Setter
@Builder
@ToString
public class OrderItemOptionDto {
    private Long optionId;         // 옵션 ID
    private Long orderItemId;
    private String optionName;    // 옵션명
    private int optionPrice;      // 옵션 가격
    private int quantity;         // 옵션 수량
}