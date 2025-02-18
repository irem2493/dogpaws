package com.dogpaws.backend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class CartSummaryResponseDto {
    @JsonProperty("cart_items")
    private List<CartItemDto> cartItems;

    @JsonProperty("total_product_price")
    private int totalProductPrice;

    @JsonProperty("delivery_fee")
    private int deliveryFee;

    @JsonProperty("total_order_price")
    private int totalOrderPrice;

    @JsonProperty("total_quantity")
    private int totalQuantity;
}