package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.CartItemParam;
import com.dogpaws.backend.dto.rim.request.CartRequestDto;
import com.dogpaws.backend.dto.rim.request.CartResponseDto;
import com.dogpaws.backend.entity.rim.*;
import com.dogpaws.backend.repository.dao.rim.CartDao;
import com.dogpaws.backend.repository.jpa.rim.*;
import com.dogpaws.frontend.exception.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
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

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;



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


    public List<CartResponseDto> getCartItems(String username) {
        return cartDao.findCartItemsByUsername(username);
    }

    @Transactional
    public void removeCartItem(String username, Long cartItemId) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        // 권한 체크
        if (!cartItem.getCart().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("해당 장바구니 상품을 삭제할 권한이 없습니다.");
        }

        // 연관된 옵션들도 함께 삭제됨 (CASCADE 설정으로)
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void updateCartItemQuantity(String username, Long cartItemId, int quantity) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        // 권한 체크
        if (!cartItem.getCart().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("해당 장바구니 상품을 수정할 권한이 없습니다.");
        }

        cartItem.updateQuantity(quantity);
    }
}