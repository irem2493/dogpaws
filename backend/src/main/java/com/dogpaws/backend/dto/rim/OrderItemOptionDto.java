package com.dogpaws.backend.dto.rim;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

// 주문 상품 옵션 DTO
@Getter
@Setter
@Builder
public class OrderItemOptionDto {
    @JsonProperty("option_id")
    private Long optionId;
    
    @JsonProperty("order_item_id")
    private Long orderItemId;
    
    @JsonProperty("option_name")
    private String optionName;
    
    @JsonProperty("option_price")
    private int optionPrice;
}