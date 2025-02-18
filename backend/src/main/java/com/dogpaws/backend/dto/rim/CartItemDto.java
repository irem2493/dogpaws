package com.dogpaws.backend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CartItemDto {
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
    private List<CartOptionDto> cartOptions;

    @JsonProperty("total_quantity")
    private int totalQuantity;

    @JsonProperty("total_price")
    private int totalPrice;
}

