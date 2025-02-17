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
import java.util.Optional;
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
    public boolean addToCart(String username, CartRequestDto requestDto) {
        // 장바구니 조회 또는 생성
        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> cartRepository.save(new Cart(username)));

        // 상품 조회
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 이미 장바구니에 같은 상품의 같은 옵션이 있는지 확인
        List<CartItem> existingCartItems = cartItemRepository.findByCartAndProduct(cart, product);
        boolean isUpdated = false;  // 업데이트 여부 추적

        if (!existingCartItems.isEmpty()) {
            for (CartRequestDto.CartOptionDto newOption : requestDto.getOptions()) {
                boolean optionExists = false;

                // 옵션 엔티티 조회
                ProductOption productOption = productOptionRepository
                        .findById(newOption.getOptionId())
                        .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다."));

                for (CartItem existingItem : existingCartItems) {
                    // ProductOption 엔티티로 찾도록
                    Optional<CartItemOption> existingOption = cartItemOptionRepository
                            .findByCartItemAndOption(existingItem, productOption);

                    if (existingOption.isPresent()) {
                        CartItemOption cartItemOption = existingOption.get();
                        cartItemOption.updateQuantity(cartItemOption.getQuantity() + newOption.getQuantity());
                        optionExists = true;
                        isUpdated = true;
                        break;
                    }
                }

                if (!optionExists) {
                    // 새로운 CartItem과 CartItemOption 생성
                    CartItem newCartItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(1)
                            .isBaseProduct("N")
                            .build();
                    cartItemRepository.save(newCartItem);

                    CartItemOption newCartItemOption = CartItemOption.builder()
                            .cartItem(newCartItem)
                            .option(productOption)
                            .quantity(newOption.getQuantity())
                            .build();
                    cartItemOptionRepository.save(newCartItemOption);
                }
            }
        } else {
            // 장바구니에 없는 새로운 상품인 경우 기존 로직대로 처리
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1)
                    .isBaseProduct("N")
                    .build();
            cartItemRepository.save(cartItem);

            for (CartRequestDto.CartOptionDto optionDto : requestDto.getOptions()) {
                ProductOption productOption = productOptionRepository
                        .findById(optionDto.getOptionId())
                        .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다."));

                CartItemOption cartItemOption = CartItemOption.builder()
                        .cartItem(cartItem)
                        .option(productOption)
                        .quantity(optionDto.getQuantity())
                        .build();
                cartItemOptionRepository.save(cartItemOption);
            }
        }
        return isUpdated;
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
                    .productId(cartItem.getProduct().getProductId())
                    .cartItemId(cartItem.getCartItemId())
                    .productName(cartItem.getProduct().getName())
                    .productImage(cartItem.getProduct().getImageUrl())
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