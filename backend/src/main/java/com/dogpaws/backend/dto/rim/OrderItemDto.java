package com.dogpaws.backend.dto.rim;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long orderItemId;
    private String qlId;
    private Long productId;
    private String productName;           // 상품명
    private String manufacturer;             // 브랜드명
    private String imageUrl;              // 상품 이미지 URL
    private Integer amount;             // 수량
    private Integer itemPrice;                // 상품 총 가격
    private List<OrderItemOptionDto> options; // 주문 옵션 목록
}