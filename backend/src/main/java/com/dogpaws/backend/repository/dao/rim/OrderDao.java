package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.OrderDto;
import com.dogpaws.backend.dto.rim.OrderItemDto;
import com.dogpaws.backend.dto.rim.OrderItemOptionDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {
    // 기존 메서드들 *
    void insertOrder(OrderDto order);
    // 주문 상품 저장 후 생성된 ID 반환
    @Options(useGeneratedKeys = true, keyProperty = "orderItemId")

    Long insertOrderItem(OrderItemDto item);

    // 주문에 연결된 장바구니 아이템 ID 조회
    List<Long> selectCartItemsByQlId(String qlId);

    // 주문 생성 시 장바구니 아이템 ID 저장
    void insertOrderCartItems(@Param("qlId") String qlId, @Param("cartItemIds") List<Long> cartItemIds);

    void insertOrderItemOption(OrderItemOptionDto option);


    // 옵션의 진행중인 주문 여부 확인
    boolean hasActiveOrders(@Param("productId") Long productId, @Param("optionId") Long optionId);

    OrderDto selectOrderByQlId(String qlId); //주문 1건 조회

    // 주문 상태와 결제 키 업데이트
    void updateOrderStatus(@Param("qlId") String qlId,
                           @Param("status") String status,
                           @Param("paymentKey") String paymentKey);

    // 주문 조회 관련
    List<OrderDto> selectOrdersByUsername(String username);


    int countOrdersByUsername(String username);

    List<OrderDto> selectOrdersByUsernameWithPaging(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("size") int size
    );







    List<OrderDto> selectOrdersByStatus(String status);
    List<OrderDto> selectRecentOrders(@Param("limit") int limit);

    // 주문 검증
    boolean existsByQlId(String qlId);



    // 주문 취소
    void cancelOrder(@Param("qlId") String qlId, @Param("reason") String reason);

    // 주문 통계
    int getTotalOrderAmount(@Param("username") String username); // 사용자 별 총 주문금액
    int getMonthlyOrderCount(@Param("username") String username, @Param("yearMonth") String yearMonth); //월별 주문 건수
}
