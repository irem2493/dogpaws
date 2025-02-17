package com.dogpaws.frontend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartOptionResponseDto {
    @JsonProperty("cart_item_id")
    private Long cartItemId;

    @JsonProperty("option_id")
    private Long optionId;

    @JsonProperty("option_name")
    private String optionName;

    @JsonProperty("option_price")
    private int optionPrice;

    private int quantity;
}