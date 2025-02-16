package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductDto> getProduct(@PathVariable Integer productId) {
        log.info("상품 상세 조회 요청: productId={}", productId);

        try {
            ProductDto productDto = productService.getProduct(productId);
            log.info("상품 상세 조회 성공: {}", productDto.getName());

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, productDto);

        } catch (IllegalArgumentException e) {
            log.error("상품을 찾을 수 없음: productId={}", productId);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);

        } catch (Exception e) {
            log.error("상품 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }

    @GetMapping
    public ApiResponse<Page<ProductListDto>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("상품 목록 조회 요청: category={}, page={}, size={}", category, page, size);

        try {
            // ProductSearchDto 생성 및 설정
            ProductSearchDto searchDto = new ProductSearchDto();
            searchDto.setMainCategory(category);
            searchDto.setPage(page + 1); // 0-based를 1-based로 변환
            searchDto.setPageSize(size);
            searchDto.setStatus("O"); // 판매중인 상품만 조회

            Page<ProductListDto> products = productService.searchProducts(searchDto);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, products);

        } catch (Exception e) {
            log.error("상품 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }

    @GetMapping("/search")
    public ApiResponse<Page<ProductListDto>> searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("상품 검색 요청: category={}, keyword={}, page={}, size={}",
                category, keyword, page, size);

        try {
            // ProductSearchDto 생성 및 설정
            ProductSearchDto searchDto = new ProductSearchDto();
            searchDto.setMainCategory(category);
            searchDto.setSearchKeyword(keyword);
            searchDto.setPage(page + 1); // 0-based를 1-based로 변환
            searchDto.setPageSize(size);

            // 검색 실행
            Page<ProductListDto> products = productService.searchProducts(searchDto);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, products);



        } catch (Exception e) {
            log.error("상품 검색 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }
}