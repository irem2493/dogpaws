package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.CartItemParam;
import com.dogpaws.backend.dto.rim.request.CartListResponseDto;
import com.dogpaws.backend.dto.rim.request.CartRequestDto;
import com.dogpaws.backend.dto.rim.request.CartSummaryResponseDto;
import com.dogpaws.backend.repository.dao.rim.CartDao;
import com.dogpaws.backend.repository.jpa.rim.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartDao cartDao;

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    //TODO : 배송비 설정 관리자에서 하도록
    public static final int DELIVERY_FEE = 3000;

    @Transactional
    public boolean addCart(String username, CartRequestDto requestDto) {
        boolean exist = false;

        if (requestDto.getOptions() != null && !requestDto.getOptions().isEmpty()) {
            // 1. 해당 상품이 장바구니에 있는지 먼저 확인
            CartItemParam existingCart = cartDao.findCartItemByProductId(username, requestDto.getProductId());
            
            for (CartRequestDto.CartOptionDto option : requestDto.getOptions()) {
                if (existingCart != null) {
                    // 2. 상품은 있고, 해당 옵션이 있는지 확인
                    CartItemParam existingOption = cartDao.findExistingCartItem(
                            username,
                            requestDto.getProductId(),
                            option.getOptionId()
                    );

                    if (existingOption != null) {
                        // 2-1. 옵션이 있으면 수량만 업데이트
                        cartDao.updateCartItemOptionQuantity(
                            existingOption.getCartItemId(), 
                            option.getOptionId(), 
                            option.getQuantity()
                        );
                    } else {
                        // 2-2. 옵션이 없으면 옵션만 추가
                        cartDao.insertCartItemOptions(
                            existingCart.getCartItemId(),
                            Collections.singletonList(option)
                        );
                    }
                    exist = true;
                }else {
                    // 3. 상품이 없으면 상품과 옵션 모두 새로 추가
                    CartItemParam param = CartItemParam.builder()
                            .username(username)
                            .productId(requestDto.getProductId())
                            .build();

                    // 상품 정보 저장
                    cartDao.insertCartItem(param);
                    log.info("생성된 cartItemId: {}", param.getCartItemId());

                    // 모든 옵션 정보 한 번에 저장
                    cartDao.insertCartItemOptions(
                            param.getCartItemId(),
                            requestDto.getOptions()  // 전체 옵션 리스트 전달
                    );

                    exist = true;
                    break;  // 상품이 생성되었으므로 더 이상의 반복은 불필요
                }
            }
        }
        return exist;
    }

    public CartSummaryResponseDto getCartSummary(String username) {
        // 1. 장바구니 목록 조회
        List<CartListResponseDto> cartItems = cartDao.findCartItemsByUsername(username);


        log.info("조회된 장바구니 아이템 수: {}", cartItems.size());
        cartItems.forEach(item -> {
            log.info("상품 ID: {}, 이름: {}, 옵션 수: {}",
                    item.getProductId(),
                    item.getProductName(),
                    item.getCartOptions().size());
        });

        // 2. 총 상품 금액 계산
        int totalProductPrice = cartItems.stream()
                .mapToInt(CartListResponseDto::getTotalPrice)
                .sum();

        // 3. 총 수량 계산
        int totalQuantity = cartItems.stream()
                .mapToInt(CartListResponseDto::getTotalQuantity)
                .sum();

        // 4. 응답 DTO 생성
        return CartSummaryResponseDto.builder()
                .cartItems(cartItems)
                .totalProductPrice(totalProductPrice)
                .deliveryFee(DELIVERY_FEE)
                .totalOrderPrice(totalProductPrice + DELIVERY_FEE)
                .totalQuantity(totalQuantity)
                .build();
    }

    /**
     * 주문 완료 후 장바구니 비우기
     */
    @Transactional
    public void clearCartAfterOrder(String username, List<Long> cartItemIds) {
        try {
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                log.warn("장바구니 아이템이 없습니다. username: {}", username);
                return;
            }
            // 먼저 옵션 삭제
            cartDao.deleteCartItemOptions(username, cartItemIds);
            // 그 다음 카트 아이템 삭제
            cartDao.deleteCartItems(username, cartItemIds);
            log.info("장바구니 비우기 완료. username: {}, items: {}", username, cartItemIds);

        } catch (Exception e) {
            log.error("장바구니 비우기 실패: {}", e.getMessage(), e);
            throw new RuntimeException("장바구니 비우기 중 오류가 발생했습니다.");
        }
    }

    /**
     * 선택된 장바구니 아이템 조회
     */
    public CartSummaryResponseDto getSelectedCartItems(String username, List<Long> cartItemIds) {
        // 선택된 장바구니 아이템만 조회
        List<CartListResponseDto> selectedItems =
                cartDao.findSelectedCartItems(username, cartItemIds);

        // 선택된 상품의 총 금액 계산
        int totalProductPrice = selectedItems.stream()
                .mapToInt(CartListResponseDto::getTotalPrice)
                .sum();

        // 선택된 상품의 총 수량 계산
        int totalQuantity = selectedItems.stream()
                .mapToInt(CartListResponseDto::getTotalQuantity)
                .sum();

        return CartSummaryResponseDto.builder()
                .cartItems(selectedItems)
                .totalProductPrice(totalProductPrice)
                .deliveryFee(DELIVERY_FEE)
                .totalOrderPrice(totalProductPrice + DELIVERY_FEE)
                .totalQuantity(totalQuantity)
                .build();
    }
}