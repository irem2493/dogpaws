package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductOptionDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import com.dogpaws.backend.dto.rim.request.ProductRegistRequest;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.common.CustomUserDetails;
import com.dogpaws.backend.service.rim.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    /**
     * 상품 등록
     */
    @PostMapping
    public ApiResponse<String> registerProduct(@ModelAttribute ProductRegistRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // JSON 문자열을 객체로 변환
        ProductDto productDto = mapper.readValue(request.getProductDtoString(), ProductDto.class);
        List<ProductOptionDto> optionDtos = mapper.readValue(request.getOptionDtosString(),
                new TypeReference<List<ProductOptionDto>>() {});

        // TODO : SecurityContext에서 현재 로그인한 사용자 정보 가져오기 -> 또는 헤더값을 검증하기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();

        productService.registProduct(
                productDto,
                optionDtos,
                request.getThumbnailImage(),
                request.getDetailImage(),
                username
        );

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "상품 등록 성공");
    }

    /**
     * 상품 조회
     */
    @GetMapping("/manage")
    public ApiResponse<List<ProductListDto>> getProductList(
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, name = "searchKeyword") String keyword) {

        log.info("keyword: {}", keyword);

        ProductSearchDto searchDto = new ProductSearchDto();
        searchDto.setMainCategory(mainCategory);
        searchDto.setStatus(status);
        searchDto.setSortBy(sortBy);
        searchDto.setSearchKeyword(keyword);

        List<ProductListDto> products = productService.searchAllProducts(searchDto);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, products);
    }

    /**
     * 상품 상태 변경
     */
    @PutMapping("/{productId}/status")
    public ApiResponse<String> updateProductStatus(
            @PathVariable Long productId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            productService.updateProductStatus(productId, status);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "상품 상태 변경 성공");
        } catch (Exception e) {
            log.error("상품 상태 변경 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "상품 상태 변경 실패");
        }
    }
}