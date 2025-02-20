package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.CartItemParam;
import com.dogpaws.backend.dto.rim.request.CartListResponseDto;
import com.dogpaws.backend.dto.rim.request.CartRequestDto;
import com.dogpaws.backend.dto.rim.request.CartResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartDao {
    //[addCart] 장바구니 생성
    void createCart(@Param("username") String username);
    //[addCart] 동일한 상품과 옵션 조합이 있는지 확인
    CartItemParam findExistingCartItem(@Param("username") String username,
                                       @Param("productId") Long productId,
                                       @Param("optionId") Long optionId);
    // 장바구니 아이템 생성
    void insertCartItem(CartItemParam param);

    //장바구니 옵션 생성
    void insertCartItemOptions(@Param("cartItemId") Long cartItemId,
                               @Param("options") List<CartRequestDto.CartOptionDto> options);

    void deleteCartItemOptions(@Param("username") String username, @Param("cartItemIds") List<Long> cartItemIds);
    void deleteCartItems(@Param("username") String username, @Param("cartItemIds") List<Long> cartItemIds);


    // 상품 ID로 장바구니 아이템 찾기
    CartItemParam findCartItemByProductId(@Param("username") String username,
                                          @Param("productId") Long productId);

    List<CartListResponseDto> findCartItemsByUsername(@Param("username") String username);


    // 장바구니 아이템 옵션 수량 업데이트
    void updateCartItemOptionQuantity(@Param("cartItemId") Long cartItemId,
                                      @Param("optionId") Long optionId,
                                      @Param("quantity") int quantity);


    // 선택된 장바구니 아이템 조회
    List<CartListResponseDto> findSelectedCartItems(
            @Param("username") String username,
            @Param("cartItemIds") List<Long> cartItemIds
    );

    /**
     * 장바구니 아이템에 해당 옵션이 이미 존재하는지 확인
     * @param cartItemId 장바구니 아이템 ID
     * @param optionId 옵션 ID
     * @return 존재하면 true, 없으면 false
     */
    boolean isOptionExists(@Param("cartItemId") Long cartItemId, @Param("optionId") Long optionId);

    void deleteSelectedItems(@Param("cartItemIds") List<Long> cartItemIds);

    void updateCartOptionQuantity(
            @Param("cartItemId") Long cartItemId,
            @Param("optionId") Long optionId,
            @Param("quantity") int quantity
    );

    boolean checkExistingCart(@Param("username") String username);

    void deleteCartOption(
            @Param("cartItemId") Long cartItemId,
            @Param("optionId") Long optionId
    );

    // 장바구니 아이템의 남은 옵션 개수 조회
    int countRemainingOptions(@Param("cartItemId") Long cartItemId);

    // 장바구니 아이템 삭제 (옵션이 없는 경우)
    void deleteEmptyCartItem(@Param("cartItemId") Long cartItemId);

}