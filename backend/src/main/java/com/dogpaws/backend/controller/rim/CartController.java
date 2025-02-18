package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.rim.request.CartAddResponse;
import com.dogpaws.backend.dto.rim.request.CartRequestDto;
import com.dogpaws.backend.dto.rim.request.CartResponseDto;
import com.dogpaws.backend.dto.rim.request.CartSummaryResponseDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<ApiResponse<CartAddResponse>> addToCart(
            @RequestBody CartRequestDto requestDto
    ) {
        log.info("=== Cart Add Request ===");
        log.info("Username: {}", requestDto.getUsername());
        log.info("ProductId: {}", requestDto.getProductId());
        log.info("Quantity: {}", requestDto.getQuantity());
        log.info("Options: {}", requestDto.getOptions());

        boolean isUpdated = cartService.addCart(requestDto.getUsername(), requestDto);


        CartAddResponse response = CartAddResponse.builder()
                .message(isUpdated ? "장바구니의 기존 상품 수량이 변경되었습니다." : "장바구니에 새로운 상품이 추가되었습니다.")
                .updated(isUpdated)
                .build();

        return ResponseEntity.ok(new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, response));
    }

    // 장바구니 목록 조회
    @GetMapping
    public CartSummaryResponseDto getCartItems(
            @RequestParam String username) {
        CartSummaryResponseDto cartSummary = cartService.getCartSummary(username);

        return cartSummary;
    }

    // 선택된 장바구니 목록 조회
    @GetMapping("/selected")
    public ApiResponse<?> getSelectedCartItems(
            @RequestParam String username,
            @RequestParam List<Long> cartItemIds) {
        try {
            CartSummaryResponseDto selectedItems =
                    cartService.getSelectedCartItems(username, cartItemIds);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, selectedItems);
        } catch (Exception e) {
            log.error("선택 상품 조회 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "선택 상품 조회에 실패했습니다."));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(ApiResponse.ApiStatus.ERROR, e.getMessage())
        );
    }
}