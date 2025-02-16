package com.dogpaws.backend.dto.rim;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductListDto {
    private Integer productId;          // 상품 ID
    private String name;                // 상품명
    private Integer price;              // 가격
    private String imageUrl;            // 대표 이미지
    private String status;              // 판매상태
    private Integer stockQuantity;      // 재고수량
    private String mainCategory;        // 대분류
    private LocalDateTime createdAt;    // 등록일
}
