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

    private String manufacturer;

    @JsonProperty("product_price")
    private int productPrice;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("cart_options")
    private List<CartOptionResponseDto> cartOptions;

    // 총 가격 계산 메서드
    public int getTotalPrice() {
        int total = 0;
        if (cartOptions != null && !cartOptions.isEmpty()) {
            // 첫 번째 옵션은 기본 상품 가격으로 계산
            CartOptionResponseDto firstOption = cartOptions.get(0);
            total += productPrice * firstOption.getQuantity();

            // 나머지 옵션들은 옵션 가격으로 계산
            for (int i = 1; i < cartOptions.size(); i++) {
                CartOptionResponseDto option = cartOptions.get(i);
                total += option.getOptionPrice() * option.getQuantity();
            }
        }
        return total;
    }
}