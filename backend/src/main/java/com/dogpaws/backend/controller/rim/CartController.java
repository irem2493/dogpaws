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
    // 선택된 장바구니 아이템 삭제
    @DeleteMapping("/selected")
    public ResponseEntity<ApiResponse<?>> deleteSelectedItems(
            @RequestBody List<Long> cartItemIds) {
        try {
            cartService.deleteSelectedItems(cartItemIds);
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponse.ApiStatus.SUCCESS,
                    Map.of("message", "선택한 상품이 삭제되었습니다.")
            ));
        } catch (Exception e) {
            log.error("장바구니 삭제 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "장바구니 삭제에 실패했습니다.")
            ));
        }
    }

    

// 장바구니 옵션 수량 업데이트
@PutMapping("/option/quantity")
public ResponseEntity<ApiResponse<?>> updateCartOptionQuantity(
        @RequestBody Map<String, Object> request) {
    try {
        // 요청 데이터 로깅
        log.info("수량 업데이트 요청 데이터: {}", request);
        
        Long cartItemId = Long.parseLong(request.get("cartItemId").toString());
        Long optionId = Long.parseLong(request.get("optionId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());

        log.info("cartItemId: {}, optionId: {}, quantity: {}", cartItemId, optionId, quantity);

        cartService.updateCartOptionQuantity(cartItemId, optionId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(
                ApiResponse.ApiStatus.SUCCESS,
                Map.of("message", "수량이 변경되었습니다.")
        ));
    } catch (IllegalArgumentException e) {
        log.error("잘못된 파라미터: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>(
                ApiResponse.ApiStatus.ERROR,
                Map.of("message", e.getMessage())
        ));
    } catch (Exception e) {
        log.error("수량 변경 실패: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ApiResponse<>(
                ApiResponse.ApiStatus.ERROR,
                Map.of("message", "수량 변경에 실패했습니다.")
        ));
    }
}

// 장바구니 옵션 삭제
@DeleteMapping("/option/{cartItemId}/{optionId}")
public ResponseEntity<ApiResponse<?>> deleteCartOption(
        @PathVariable Long cartItemId,
        @PathVariable Long optionId) {
    try {
        log.info("옵션 삭제 요청 - cartItemId: {}, optionId: {}", cartItemId, optionId);

        cartService.deleteCartOption(cartItemId, optionId);
        return ResponseEntity.ok(new ApiResponse<>(
                ApiResponse.ApiStatus.SUCCESS,
                Map.of("message", "옵션이 삭제되었습니다.")
        ));
    } catch (Exception e) {
        log.error("옵션 삭제 실패: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ApiResponse<>(
                ApiResponse.ApiStatus.ERROR,
                Map.of("message", "옵션 삭제에 실패했습니다.")
        ));
    }
}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(ApiResponse.ApiStatus.ERROR, e.getMessage())
        );
    }
}