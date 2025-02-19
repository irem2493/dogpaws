package com.dogpaws.frontend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartOptionResponseDto {
    @JsonProperty("cart_item_id")
    private Long cartItemId;

    @JsonProperty("option_id")
    private Long optionId;

    private Long cartItemOptionId;

    @JsonProperty("option_name")
    private String optionName;

    @JsonProperty("option_price")
    private int optionPrice;

    private int quantity;
}