package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.rim.request.CartRequestDto;
import com.dogpaws.backend.dto.rim.request.CartResponseDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addToCart(
            @RequestBody CartRequestDto requestDto
    ) {
        log.info("=== Cart Add Request ===");
        log.info("Username: {}", requestDto.getUsername());
        log.info("ProductId: {}", requestDto.getProductId());
        log.info("Quantity: {}", requestDto.getQuantity());
        log.info("Options: {}", requestDto.getOptions());

        cartService.addToCart(requestDto.getUsername(), requestDto);

        return ResponseEntity.ok(
                new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "장바구니에 추가되었습니다.")
        );
    }

    // 장바구니 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartResponseDto>>> getCartItems(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CartResponseDto> cartItems = cartService.getCartItems(userDetails.getUsername());
        return ResponseEntity.ok(
                new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, cartItems)
        );
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> removeCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId
    ) {
        cartService.removeCartItem(userDetails.getUsername(), cartItemId);
        return ResponseEntity.ok(
                new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "장바구니 상품이 삭제되었습니다.")
        );
    }

    // 장바구니 상품 수량 수정
    @PatchMapping("/{cartItemId}/quantity")
    public ResponseEntity<ApiResponse<String>> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        cartService.updateCartItemQuantity(userDetails.getUsername(), cartItemId, quantity);
        return ResponseEntity.ok(
                new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "수량이 변경되었습니다.")
        );
    }

    // 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(ApiResponse.ApiStatus.ERROR, e.getMessage())
        );
    }
}