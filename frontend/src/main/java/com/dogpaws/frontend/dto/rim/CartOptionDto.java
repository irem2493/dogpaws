package com.dogpaws.frontend.dto.rim;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartOptionDto {
    private Long optionId;
    private String optionName;
    private Integer optionPrice;
    private Integer quantity;
}