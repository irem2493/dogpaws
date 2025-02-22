package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductOptionDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import com.dogpaws.backend.entity.rim.Product;
import com.dogpaws.backend.entity.rim.ProductOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDao {
    // 상품 검색 (페이징, 검색 조건 적용)
    List<ProductListDto> searchProducts(ProductSearchDto searchDto);

    // 검색 조건에 맞는 전체 건수 조회
    int getTotalCount(ProductSearchDto searchDto);

    // 상품 ID로 옵션 목록 조회하는 메서드 추가
    List<ProductOption> findByProductProductId(Long productId);

    List<ProductListDto> getBestProducts(@Param("category") String category,
                                         @Param("size") int size);
    // 주문 진행중인 상품인지 확인
    boolean hasActiveOrders(@Param("productId") Long productId);

    // 상품 조회 (비관적 락)
    Product findByIdWithLock(@Param("productId") Long productId);



    // 상품의 전체 옵션 재고 합계 조회
    int getTotalStockByProductId(@Param("productId") Long productId);


    /**
     * 옵션 조회 (비관적 락)
     */
    ProductOption findOptionByIdWithLock(@Param("optionId") Long optionId);
    /**
     * 옵션명만 수정
     */
    void updateOptionName(@Param("optionId") Long optionId, @Param("optionName") String optionName);

    /**
     * 옵션 전체 정보 수정
     */
    void updateOption(ProductOptionDto option);

    /**
     * 옵션 상태 업데이트
     */
    void updateOptionStatus(@Param("optionId") Long optionId, @Param("status") String status);

    /**
     * 옵션 삭제
     */
    void deleteOption(@Param("optionId") Long optionId);
}
