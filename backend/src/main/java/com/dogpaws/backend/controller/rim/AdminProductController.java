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

    @GetMapping("/manage")
    public ApiResponse<Page<ProductListDto>> getProductList(
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ProductSearchDto searchDto = new ProductSearchDto();
        searchDto.setMainCategory(mainCategory);
        searchDto.setSubCategory(subCategory);
        searchDto.setStatus(status);
        searchDto.setSortBy(sortBy);
        searchDto.setSearchKeyword(searchKeyword);
        searchDto.setPage(page + 1);
        searchDto.setPageSize(size);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,
                productService.searchProducts(searchDto));
    }

    // 상품 상태 변경
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

    /**
     * 상품 재고 수정 처리 (입고/출고)
     */
    @PostMapping("/{productId}/stock")
    public ApiResponse<Map<String, Object>> updateStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        try {
            Long optionId = Long.parseLong(request.get("optionId").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            Boolean isIncrease = Boolean.parseBoolean(request.get("isIncrease").toString());

            // 재고 수정 처리
            productService.updateStock(productId, optionId, quantity, isIncrease);

            // 현재 재고 상태 조회
            Map<String, Integer> currentStock = productService.getCurrentStock(productId, optionId);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("message", isIncrease ? "재고 입고 처리 성공" : "재고 출고 처리 성공");
            response.put("optionStock", currentStock.get("optionStock"));
            response.put("totalStock", currentStock.get("totalStock"));

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, response);

        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: {}", e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "재고 처리 실패: " + e.getMessage()));

        } catch (Exception e) {
            log.error("재고 처리 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "재고 처리 실패: 시스템 오류"));
        }
    }

    /**
     * 현재 재고 상태 조회
     */
    @GetMapping("/{productId}/stock/{optionId}")
    public ApiResponse<Map<String, Integer>> getStock(
            @PathVariable Long productId,
            @PathVariable Long optionId) {
        try {
            Map<String, Integer> stockInfo = productService.getCurrentStock(productId, optionId);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, stockInfo);

        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: {}", e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);

        } catch (Exception e) {
            log.error("재고 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }


    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ApiResponse<String> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "상품 삭제 성공");
        } catch (IllegalStateException e) {
            log.warn("상품 삭제 실패 (주문 진행중): {}", e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("상품 삭제 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "상품 삭제 실패");
        }
    }
}