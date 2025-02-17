package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.request.CartRequestDto;
import com.dogpaws.backend.dto.rim.request.CartResponseDto;
import com.dogpaws.backend.entity.rim.*;
import com.dogpaws.backend.repository.jpa.rim.*;
import com.dogpaws.frontend.exception.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;


    @Transactional
    public void addToCart(String username, CartRequestDto requestDto) {
        // 장바구니 조회 또는 생성
        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> cartRepository.save(new Cart(username)));

        // 상품 조회
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 장바구니 상품 생성
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(requestDto.getQuantity())
                .isBaseProduct(requestDto.isBaseProduct() ? "Y" : "N")
                .build();

        cartItemRepository.save(cartItem);

        // 옵션이 있는 경우 옵션 추가
        if (requestDto.getOptions() != null && !requestDto.getOptions().isEmpty()) {
            for (CartRequestDto.CartOptionDto optionDto : requestDto.getOptions()) {
                ProductOption productOption = productOptionRepository.findById(optionDto.getOptionId())
                        .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다."));

                CartItemOption cartItemOption = CartItemOption.builder()
                        .cartItem(cartItem)
                        .option(productOption)
                        .quantity(optionDto.getQuantity())
                        .build();

                cartItemOptionRepository.save(cartItemOption);
            }
        }
    }

    public List<CartResponseDto> getCartItems(String username) {
        // 사용자의 장바구니 조회
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));

        // 장바구니 상품 목록 조회
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        return cartItems.stream().map(cartItem -> {
            // 옵션 정보 조회
            List<CartItemOption> options = cartItemOptionRepository.findByCartItem(cartItem);

            // 기본 가격 계산
            int basePrice = cartItem.getProduct().getPrice() * cartItem.getQuantity();

            // 옵션 정보 변환
            List<CartResponseDto.CartOptionInfo> optionInfos = options.stream()
                    .map(option -> CartResponseDto.CartOptionInfo.builder()
                            .optionName(option.getOption().getOptionName())
                            .optionPrice(option.getOption().getOptionPrice())
                            .quantity(option.getQuantity())
                            .build())
                    .collect(Collectors.toList());

            // 총 가격 계산 (기본 가격 + 옵션 가격)
            int totalPrice = basePrice + optionInfos.stream()
                    .mapToInt(opt -> opt.getOptionPrice() * opt.getQuantity())
                    .sum();

            return CartResponseDto.builder()
                    .cartItemId(cartItem.getCartItemId())
                    .productName(cartItem.getProduct().getName())
                    .basePrice(cartItem.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .options(optionInfos)
                    .totalPrice(totalPrice)
                    .build();
        }).collect(Collectors.toList());
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