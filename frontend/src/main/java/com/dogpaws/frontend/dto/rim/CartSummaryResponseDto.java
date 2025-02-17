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
public class CartSummaryResponseDto {
    @JsonProperty("cart_items")
    private List<CartListResponseDto> cartItems;

    @JsonProperty("total_product_price")
    private int totalProductPrice;

    @JsonProperty("delivery_fee")
    private int deliveryFee;

    @JsonProperty("total_order_price")
    private int totalOrderPrice;

    @JsonProperty("total_quantity")
    private int totalQuantity;
}