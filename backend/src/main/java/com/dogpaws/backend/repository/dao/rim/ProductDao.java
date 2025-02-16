package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDao {
    // 상품 검색 (페이징, 검색 조건 적용)
    List<ProductListDto> searchProducts(ProductSearchDto searchDto);

    // 검색 조건에 맞는 전체 건수 조회
    int getTotalCount(ProductSearchDto searchDto);
}
