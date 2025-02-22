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
            @RequestParam(required = false) String keyword) {

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
     * 현재 재고 상태 조회 (상품 ID로 모든 옵션 재고 포함)
     */
    @GetMapping("/{productId}/stock")
    public ApiResponse<Map<String, Object>> getStock(@PathVariable Long productId) {
        try {
            Map<String, Object> stockInfo = productService.getAllStockInfo(productId);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, stockInfo);
        } catch (Exception e) {
            log.error("재고 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductDto> getProduct(@PathVariable Long productId) {
        try {
            ProductDto product = productService.getProduct(productId);

            // 각 옵션별 주문 진행 여부 확인
            List<ProductOptionDto> options = product.getOptions();
            if (options != null) {
                for (ProductOptionDto option : options) {
                    boolean hasActiveOrders = productService.checkActiveOrders(productId, option.getOptionId().longValue());
                    option.setHasActiveOrders(hasActiveOrders);
                }
            }

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, product);
        } catch (Exception e) {
            log.error("상품 조회 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }

    /**
     * 상품 간단 수정
     */
    @PutMapping("/{productId}/simple")
    public ApiResponse<String> updateProductSimple(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        try {
            productService.updateProductSimple(
                    productId,
                    (String) request.get("name"),
                    (Integer) request.get("price"),
                    (String) request.get("status"),
                    (String) request.get("description")
            );
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "상품 수정 성공");
        } catch (Exception e) {
            log.error("상품 수정 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "상품 수정 실패");
        }
    }


    /**
     * 상품 수정 TODO: 고치는중
     */
    @PutMapping("/{productId}")
    public ApiResponse<String> updateProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductRegistRequest request) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductDto productDto = mapper.readValue(request.getProductDtoString(), ProductDto.class);

            // 현재 로그인한 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();

            productService.updateProduct(
                    productId,
                    productDto,
                    request.getThumbnailImage(),
                    request.getDetailImage(),
                    username
            );

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "상품 수정 성공");
        } catch (Exception e) {
            log.error("상품 수정 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "상품 수정 실패: " + e.getMessage());
        }
    }

    /**
     * 상품 삭제
     */
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