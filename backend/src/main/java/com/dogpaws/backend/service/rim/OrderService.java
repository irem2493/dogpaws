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
        String tossOrderId;
        do {
            tossOrderId = generateOrderNumber();
        } while (orderDao.existsByQlId(tossOrderId));

        // OrderDto 생성
        log.info("=== OrderDto 필드 디버깅 ===");
        log.info("qlId: {}", tossOrderId);
        log.info("username: {}", request.getUsername());
        log.info("totalPrice: {}", request.getTotalPrice());
        log.info("ordererName: {}", request.getOrderName());
        log.info("ordererPhone: {}", request.getOrderPhone());
        log.info("shippingZipcode: {}", request.getShippingZipcode());
        log.info("shippingAddress1: {}", request.getShippingAddress1());
        log.info("shippingAddress2: {}", request.getShippingAddress2());
        log.info("shippingExtraAddress: {}", request.getShippingExtraAddress());
        log.info("receiverName: {}", request.getReceiverName());
        log.info("receiverPhone: {}", request.getReceiverPhone());
        log.info("shippingMemo: {}", request.getShippingMemo());
        log.info("orderStatus: {}", OrderStatus.READY);
        log.info("orderDate: {}", LocalDateTime.now());

        OrderDto order = OrderDto.builder()
                .qlId(tossOrderId)
                .username(request.getUsername())
                .totalPrice(request.getTotalPrice())
                .orderName(request.getOrderName())
                .orderPhone(request.getOrderPhone())
                .shippingZipcode(request.getShippingZipcode())
                .shippingAddress1(request.getShippingAddress1())
                .shippingAddress2(request.getShippingAddress2())
                .shippingExtraAddress(request.getShippingExtraAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingMemo(request.getShippingMemo())
                .orderStatus(OrderStatus.READY)
                .build();



        // 상세 디버깅 로그 추가
        log.info("=== 주문 생성 요청 상세 정보 ===");
        log.info("username: {}", request.getUsername());
        log.info("totalPrice: {}", request.getTotalPrice());
        log.info("orderName: {}", request.getOrderName());
        log.info("orderPhone: {}", request.getOrderPhone());
        log.info("shippingZipcode: {}", request.getShippingZipcode());
        log.info("shippingAddress1: {}", request.getShippingAddress1());
        log.info("shippingAddress2: {}", request.getShippingAddress2());
        log.info("shippingMemo: {}", request.getShippingMemo());
        log.info("cartItems: {}", request.getCartItems());

        if (request.getCartItems() == null) {
            log.error("cartItems is null!");
            throw new IllegalArgumentException("장바구니 아이템 정보가 없습니다.");
        }

        // 주문 정보 저장
        orderDao.insertOrder(order);
        log.info("주문정보 저장 완료");

        // 주문 상품 정보 저장
        for (CartItemDto cartItem : request.getCartItems()) {
            OrderItemDto orderItem = OrderItemDto.builder()
                    .qlId(tossOrderId)
                    .productId(cartItem.getProductId())
                    .amount(cartItem.getTotalQuantity())
                    .itemPrice(cartItem.getTotalPrice())
                    .build();

            orderDao.insertOrderItem(orderItem);
            log.info("주문 상품정보 저장 완료");

            // 주문 상품 옵션 정보 저장
            for (CartOptionDto option : cartItem.getCartOptions()) {
                OrderItemOptionDto orderItemOption = OrderItemOptionDto.builder()
                        .orderItemId(orderItem.getOrderItemId())
                        .optionName(option.getOptionName())
                        .optionPrice(option.getOptionPrice())
                        .build();

                orderDao.insertOrderItemOption(orderItemOption);
                log.info("주문 상품정보 옵션 저장 완료");
            }
        }

        // 장바구니 비우기
        List<Long> cartItemIds = request.getCartItems().stream()
                .map(CartItemDto::getCartItemId)
                .collect(Collectors.toList());

        cartService.clearCartAfterOrder(request.getUsername(), cartItemIds);

        log.info("기존 장바구니 비우기 완료");

        return order;
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
