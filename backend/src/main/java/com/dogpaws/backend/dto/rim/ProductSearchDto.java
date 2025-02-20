package com.dogpaws.backend.dto.rim;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Data
public class ProductSearchDto {
    private String mainCategory;    // 대분류 (F:사료, N:간식, T:장난감)
    private String subCategory;     // 소분류 (사료만: D:건식, W:습식)
    private String searchKeyword;   // 검색어
    private String status;          // 판매상태 (O:판매중, S:품절, D:판매중지)
    private Integer page = 1;       // 현재 페이지
    private Integer pageSize = 10;  // 페이지당 항목 수
    private Integer offset;         // 페이지 오프셋

    // 정렬 옵션
    private String sortBy = "created_at";  // 정렬 기준
    private String sortDirection = "DESC"; // 정렬 방향

    // Spring Data JPA Pageable 생성
    public Pageable getPageable() {
        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("ASC") ?
                        Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );
        return PageRequest.of(page - 1, pageSize, sort);
    }
    // offset 계산 메서드
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

}