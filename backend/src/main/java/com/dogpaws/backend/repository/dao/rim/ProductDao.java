package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDao {
    // 상품 목록 조회 (검색 조건 적용)
    List<ProductListDto> getProducts(ProductSearchDto searchDto);
    // 전체 건수 조회 (페이징용)
    int getTotalCount(ProductSearchDto searchDto);
}
