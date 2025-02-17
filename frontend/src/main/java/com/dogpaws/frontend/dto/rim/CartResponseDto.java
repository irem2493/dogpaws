package com.dogpaws.frontend.dto.rim;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private Long productId;
    private String productName;
    private Integer productPrice;
    private String productImage;
    private Integer baseQuantity;
    private List<CartOptionDto> options;
}
