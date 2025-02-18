package com.dogpaws.backend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartOptionDto {
    private int quantity;

    private Long cartItemId;

    private Long optionId;

    private String optionName;

    private int optionPrice;
}