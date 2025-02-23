package com.dogpaws.backend.dto.rim.request;

import com.dogpaws.backend.dto.rim.ProductOptionDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartListResponseDto {
    private Long cartItemId;
    private String manufacturer;
    private Long productId;
    private String productName;
    private int productPrice;
    private String imageUrl;
    private List<CartOptionResponseDto> cartOptions;
    private List<ProductOptionDto> availableOptions;

    // 총 금액 계산 메서드
    public int getTotalPrice() {
        return cartOptions.stream()
<<<<<<< HEAD
                .mapToInt(option -> option.getOptionPrice() * option.getQuantity())
=======
                .mapToInt(option -> (productPrice + option.getOptionPrice()) * option.getQuantity())
>>>>>>> origin/REQ-68-관리자
                .sum();
    }

    // 총 수량 계산 메서드
    public int getTotalQuantity() {
        return cartOptions.stream()
                .mapToInt(CartOptionResponseDto::getQuantity)
                .sum();
    }
}