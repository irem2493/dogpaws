package com.dogpaws.frontend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartListResponseDto {

    @JsonProperty("cart_item_id")
    private Long cartItemId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_price")
    private int productPrice;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("cart_options")
    private List<CartOptionResponseDto> cartOptions;
}