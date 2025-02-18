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
    private Long cartItemId;

    private Long productId;

    private String productName;

    private int productPrice;

    private String imageUrl;

    private List<CartOptionDto> cartOptions;

    private int totalQuantity;

    private int totalPrice;
}

