package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.OrderDto;
import com.dogpaws.backend.dto.rim.OrderItemDto;
import com.dogpaws.backend.dto.rim.OrderItemOptionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDao {
    // 기존 메서드들 *
    void insertOrder(OrderDto order);
    void insertOrderItem(OrderItemDto orderItem);
    void insertOrderItemOption(OrderItemOptionDto option);
    OrderDto selectOrderByQlId(String qlId); //주문 1건 조회
    void updateOrderStatus(@Param("qlId") String qlId, @Param("status") String status);

    // 결제 관련 *
    void updatePaymentKey(@Param("qlId") String qlId, @Param("paymentKey") String paymentKey);

    // 주문 조회 관련
    List<OrderDto> selectOrdersByUsername(String username);
    List<OrderDto> selectOrdersByStatus(String status);
    List<OrderDto> selectRecentOrders(@Param("limit") int limit);

    // 주문 검증
    boolean existsByQlId(String qlId);
    int countOrdersByUsername(String username);

    // 주문 취소
    void cancelOrder(@Param("qlId") String qlId, @Param("reason") String reason);

    // 주문 통계
    int getTotalOrderAmount(@Param("username") String username); // 사용자 별 총 주문금액
    int getMonthlyOrderCount(@Param("username") String username, @Param("yearMonth") String yearMonth); //월별 주문 건수
}
