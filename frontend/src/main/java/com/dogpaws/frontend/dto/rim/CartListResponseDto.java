package com.dogpaws.frontend.dto.rim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartListResponseDto {

    @JsonProperty("cart_item_id")
    private Long cartItemId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    private String manufacturer;

    @JsonProperty("product_price")
    private int productPrice;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("cart_options")
    private List<CartOptionResponseDto> cartOptions;

    private List<ProductOptionDto> availableOptions;

    public int getTotalPrice() {
        return cartOptions.stream()
                .mapToInt(option -> (productPrice + option.getOptionPrice()) * option.getQuantity())
                .sum();
    }
}