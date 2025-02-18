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
    private static final int DELIVERY_FEE = 3000;

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
                } else {
                    // 3. 상품이 없으면 상품과 옵션 모두 새로 추가
                    CartItemParam param = CartItemParam.builder()
                            .username(username)
                            .productId(requestDto.getProductId())
                            .build();
                    
                    cartDao.insertCartItem(param);
                    log.info("생성된 cartItemId: {}", param.getCartItemId());
                    log.info("옵션 정보: {}", option);

                    cartDao.insertCartItemOptions(
                        param.getCartItemId(),
                        Collections.singletonList(option)
                    );
                    break; // 첫 옵션에서 상품을 생성했으므로 이후 옵션은 위의 로직으로 처리됨
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



}