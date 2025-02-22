package com.dogpaws.backend.dto.rim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionDto {
    private Integer optionId;        // 옵션 ID (auto increment)
    private Long productId;       // 상품 ID (FK)
    private String optionName;       // 옵션명 (예: "블랙-L", "레드-M")
    private Integer optionPrice;     // 옵션 추가 금액 (기본 상품 가격에 추가되는 금액)
    private Integer optionStock;     // 옵션 재고수량
    private LocalDateTime createAt;  // 생성일시
    private LocalDateTime updateAt;  // 수정일시
    private String optionSize;
    private String optionColor;
    private String optionWeight;
    private String optionMaterial;
    private String optionExpirationDate;
    private String optionStorageInfo;
    private String optionManufacturer;
    private String optionOrigin;
    private boolean isBaseOption;

    private String status;  // 'O': 판매중, 'S': 품절, 'D': 판매중지

    private boolean hasActiveOrders;

    private Integer costPrice; //옵션 원가

    public boolean isBaseOption() {
        return isBaseOption;
    }

    public void setBaseOption(boolean baseOption) {
        this.isBaseOption = baseOption;
    }
}