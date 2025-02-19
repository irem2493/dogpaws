package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.*;
import com.dogpaws.backend.repository.dao.rim.OrderDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dogpaws.backend.dto.rim.OrderItemOptionDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDao orderDao;
    private final CartService cartService;


    /**
     * 주문 생성 (결제 전)
     */
    @Transactional
    public OrderDto createOrder(OrderDto request) {
        log.info("주문 생성 시작 (결제 전) - 요청 데이터: {}", request);

        //1. 주문 아이템 검증
        if (request.getOrderItems() == null) {
            log.error("cartItems is null!");
            throw new IllegalArgumentException("주문 아이템 정보가 없습니다.");
        }

        //2. 주문번호 생성
        String tossOrderId;
        do {
            tossOrderId = generateOrderNumber();
        } while (orderDao.existsByQlId(tossOrderId));

        //3. 주문 객체 생성
        OrderDto order = OrderDto.builder()
                .qlId(tossOrderId)
                .username(request.getUsername())
                .totalPrice(request.getTotalPrice())
                .orderName(request.getOrderName())
                .orderPhone(request.getOrderPhone())
                .shippingZipcode(request.getShippingZipcode())
                .shippingAddress1(request.getShippingAddress1())
                .productPrice(request.getTotalPrice() - CartService.DELIVERY_FEE)
                .deliveryFee(CartService.DELIVERY_FEE)
                .shippingAddress2(request.getShippingAddress2())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingMemo(request.getShippingMemo())
                .orderStatus(OrderStatus.READY)
                .build();

        try {
            // 4. 주문 정보 저장
            orderDao.insertOrder(order);
            log.info("주문 정보 저장 완료");

            // 5. 주문 상품 정보 저장
            saveOrderItems(order.getQlId(), request.getOrderItems());
            log.info("주문 상품 정보 저장 완료");


            return order;

        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("주문 생성 실패", e);
        }
    }

    /**
     * 주문 상태 업데이트
     */
    @Transactional
    public void updateOrderStatus(String qlId, OrderStatus status, String paymentKey) {
        log.info("주문 상태 업데이트 시작: qlId={}, status={}, paymentKey={}", qlId, status, paymentKey);

        try {
            OrderDto order = orderDao.selectOrderByQlId(qlId);
            log.info("qlID로 주문내역 찾기 완료");
            if (order == null) {
                throw new IllegalArgumentException("주문을 찾을 수 없습니다: " + qlId);
            }

            // 결제 완료 상태로 변경 시 장바구니 비우기
            if (status == OrderStatus.PAID) {
                try {
                    // 장바구니 아이템 ID 조회
                    List<Long> cartItemIds = orderDao.selectCartItemsByQlId(qlId);
                    log.info("장바구니 아이템 ID 조회 완료: {}", cartItemIds);

                    // 장바구니 비우기
                    if (cartItemIds != null && !cartItemIds.isEmpty()) {
                        cartService.clearCartAfterOrder(order.getUsername(), cartItemIds);
                        log.info("장바구니 비우기 완료");
                    }
                } catch (Exception e) {
                    log.error("장바구니 비우기 실패: {}", e.getMessage(), e);
                    throw new RuntimeException("장바구니 비우기 실패", e);
                }
            }

            // 주문 상태 및 결제 키 업데이트
            orderDao.updateOrderStatus(qlId, status.name(), paymentKey);
            log.info("주문 상태 업데이트 완료: {}", status);

        } catch (Exception e) {
            log.error("주문 상태 업데이트 실패: {}", e.getMessage(), e);
            throw new RuntimeException("주문 상태 업데이트 실패", e);
        }
    }

    /**
     * 주문 상품 정보 저장
     */
    private void saveOrderItems(String qlId, List<OrderItemDto> orderItems) {
        if (orderItems == null) {
            throw new IllegalArgumentException("주문 아이템 정보가 없습니다.");
        }

        for (OrderItemDto orderItem : orderItems) {
            // 주문 상품 저장
            OrderItemDto backendOrderItem = OrderItemDto.builder()
                    .qlId(qlId)
                    .productId(orderItem.getProductId())
                    .amount(orderItem.getAmount())
                    .itemPrice(orderItem.getItemPrice())
                    .build();

            // orderDao.insertOrderItem 실행 후 생성된 orderItemId를 backendOrderItem에 설정
            orderDao.insertOrderItem(backendOrderItem);
            Long orderItemId = backendOrderItem.getOrderItemId();  // 자동 생성된 ID 가져오기

            // 주문 상품 옵션 저장
            if (orderItem.getOptions() != null) {
                for (OrderItemOptionDto option : orderItem.getOptions()) {
                    OrderItemOptionDto backendOption = OrderItemOptionDto.builder()
                            .orderItemId(orderItemId)
                            .optionName(option.getOptionName())
                            .optionPrice(option.getOptionPrice())
                            .quantity(option.getQuantity())
                            .build();

                    orderDao.insertOrderItemOption(backendOption);
                }
            }
        }
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
        OrderDto order = orderDao.selectOrderByQlId(qlId);
        order.setDeliveryFee(CartService.DELIVERY_FEE);
        return order;
    }

    /**
     * 사용자의 주문 목록 조회
     */
    public List<com.dogpaws.backend.dto.rim.OrderDto> getUserOrders(String username) {
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

        if (order.getOrderStatus() != OrderStatus.READY && order.getOrderStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("취소할 수 없는 주문 상태입니다: " + order.getOrderStatus());
        }

        try {
            orderDao.cancelOrder(qlId, reason);
            log.info("주문 취소 완료: orderId={}, reason={}", qlId, reason);
        } catch (Exception e) {
            log.error("주문 취소 실패: {}", e.getMessage(), e);
            throw new RuntimeException("주문 취소 실패", e);
        }

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
