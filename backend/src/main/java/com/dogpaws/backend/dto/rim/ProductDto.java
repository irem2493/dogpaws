package com.dogpaws.backend.dto.rim;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDto {
    private Integer productId;          // 상품 ID
    private String name;                // 상품명
    private Integer price;              // 가격
    private Integer stockQuantity;      // 재고 수량
    private String description;         // 상품 설명
    private String imageUrl;            // 상품 대표 이미지 URL
    private String imageDetailUrl;      // 상품 상세 이미지 URL
    private String status;              // 상품 상태 (O:판매중, S:품절, D:판매중지)
    private String size;                // 크기 (용량)
    private String material;            // 소재/성분
    private String origin;              // 원산지
    private String expirationDate; // LocalDate 대신 String으로 변경
    private String color;               // 색상
    private LocalDateTime createdAt;    // 등록 날짜
    private LocalDateTime updatedAt;    // 수정 날짜
    private String mainCategory;        // 대분류 (M:산책용품, K:간식, S:사료)
    private String subCategory;         // 소분류 (사료만: D:건식, W:습식)
    private String storageInfo;         // 보관방법
    private String weight;              // 무게
    private String manufacturer;

    private List<ProductOptionDto> options;  // 상품 옵션 목록

    // 판매 상태 enum
    public enum Status {
        ON_SALE("O"),      // 판매중
        SOLD_OUT("S"),     // 품절
        DISCONTINUED("D");  // 판매중지
        
        private final String code;
        
        Status(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
    }
    // 카테고리 enum
    public enum Category {
        FOOD("F"),         // 사료
        SNACK("N"),        // 간식
        TOY("T");          // 장난감
        
        private final String code;
        
        Category(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
    }
}