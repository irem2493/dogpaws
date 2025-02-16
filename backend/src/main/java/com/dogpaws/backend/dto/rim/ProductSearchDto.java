package com.dogpaws.backend.dto.rim;

import lombok.Data;

@Data
public class ProductSearchDto {
    private String mainCategory;    // 대분류
    private String searchKeyword;   // 검색어
    private String status;          // 판매상태
    private Integer page = 1;       // 현재 페이지
    private Integer pageSize = 10;  // 페이지당 항목 수

    // 페이징 처리를 위한 offset 계산
    public int getOffset() {
        return (page - 1) * pageSize;
    }
}