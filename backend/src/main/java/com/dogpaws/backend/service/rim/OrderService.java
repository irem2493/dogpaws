package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.*;
import com.dogpaws.backend.dto.rim.request.OrderCreateRequest;
import com.dogpaws.backend.repository.dao.rim.OrderDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDao orderDao;
    private final CartService cartService;


    /**
     * 주문 생성
     */
    @Transactional
    public OrderDto createOrder(OrderCreateRequest request) {
        // 주문번호 생성
        String orderNumber;
        do {
            orderNumber = generateOrderNumber();
        } while (orderDao.existsByQlId(orderNumber));

        // OrderDto 생성
        OrderDto order = OrderDto.builder()
                .qlId(orderNumber)
                .username(request.getUsername())
                .totalPrice(request.getTotalPrice())
                .ordererName(request.getOrdererName())
                .ordererPhone(request.getOrdererPhone())
                .shippingZipcode(request.getShippingZipcode())
                .shippingAddress1(request.getShippingAddress1())
                .shippingAddress2(request.getShippingAddress2())
                .shippingExtraAddress(request.getShippingExtraAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingMemo(request.getShippingMemo())
                .orderStatus(OrderStatus.READY)
                .orderDate(LocalDateTime.now())
                .build();

        // CartItemDto에서 직접 cartItemId 추출(장바구니 비우기)
        List<Long> cartItemIds = request.getCartItems().stream()
                .map(CartItemDto::getCartItemId)
                .collect(Collectors.toList());

        // 주문 정보 저장
        orderDao.insertOrder(order);

        // 주문 상품 정보 저장
        for (CartItemDto cartItem : request.getCartItems()) {
            OrderItemDto orderItem = OrderItemDto.builder()
                    .qlId(orderNumber)
                    .productId(cartItem.getProductId())
                    .amount(cartItem.getTotalQuantity())
                    .itemPrice(cartItem.getTotalPrice())
                    .build();

            orderDao.insertOrderItem(orderItem);

            // 주문 상품 옵션 정보 저장
            for (CartOptionDto option : cartItem.getCartOptions()) {
                OrderItemOptionDto orderItemOption = OrderItemOptionDto.builder()
                        .orderItemId(orderItem.getOrderItemId())
                        .optionName(option.getOptionName())
                        .optionPrice(option.getOptionPrice())
                        .build();

                orderDao.insertOrderItemOption(orderItemOption);
            }
        }

        // 장바구니 비우기
        cartService.clearCartAfterOrder(request.getUsername(), cartItemIds);

        return orderDao.selectOrderByQlId(orderNumber);
    }
    /**
     * 주문번호 생성
     */
    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String randomPart = String.format("%04d", ThreadLocalRandom.current().nextInt(1, 10000));

        return String.format("%s-%s-%s", datePart, timePart, randomPart);
    }
    /**
     * 주문 조회 -  결제 완료 후 주문 상세 페이지
     */
    public OrderDto getOrder(String qlId) {
        return orderDao.selectOrderByQlId(qlId);
    }

    /**
     * 사용자의 주문 목록 조회
     */
    public List<OrderDto> getUserOrders(String username) {
        return orderDao.selectOrdersByUsername(username);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(String qlId, String reason) {
        OrderDto order = orderDao.selectOrderByQlId(qlId);
        if (order == null) {
            throw new IllegalArgumentException("주문을 찾을 수 없습니다: " + qlId);
        }

        if (!"READY".equals(order.getOrderStatus()) && !"PAID".equals(order.getOrderStatus())) {
            throw new IllegalStateException("취소할 수 없는 주문 상태입니다: " + order.getOrderStatus());
        }

        orderDao.cancelOrder(qlId, reason);
    }


    /**
     * 사용자의 주문 통계
     */
    public OrderStatistics getUserOrderStatistics(String username) {
        int totalAmount = orderDao.getTotalOrderAmount(username);
        int monthlyCount = orderDao.getMonthlyOrderCount(
                username,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"))
        );

        return OrderStatistics.builder()
                .totalOrderAmount(totalAmount)
                .monthlyOrderCount(monthlyCount)
                .build();
    }
}
