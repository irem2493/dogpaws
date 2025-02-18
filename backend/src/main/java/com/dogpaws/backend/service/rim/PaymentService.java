package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.client.TossPaymentClient;
import com.dogpaws.backend.dto.rim.OrderDto;
import com.dogpaws.backend.dto.rim.OrderStatus;
import com.dogpaws.backend.dto.rim.request.PaymentResponse;
import com.dogpaws.backend.exception.PaymentException;
import com.dogpaws.backend.repository.dao.rim.OrderDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final OrderService orderService;
    private final OrderDao orderDao;


    @Transactional
    public void confirmPayment(String paymentKey, String orderId, Integer amount) {
        try {
            // 1. 주문 정보 검증
            OrderDto order = orderService.getOrder(orderId);
            validatePaymentInfo(order, amount);

            // 2. 토스페이먼츠 결제 승인 요청
            PaymentResponse paymentResponse =
                    tossPaymentClient.requestPaymentConfirm(paymentKey, orderId, amount);

            // 3. 주문 상태 업데이트 및 결제키 저장
            orderDao.updatePaymentKey(orderId, paymentKey);
            orderDao.updateOrderStatus(orderId, OrderStatus.PAID.name());

            log.info("결제 승인 성공: orderId={}, paymentKey={}", orderId, paymentKey);

        } catch (Exception e) {
            log.error("결제 처리 실패: {}", e.getMessage(), e);
            throw new PaymentException("결제 처리 중 오류가 발생했습니다.");
        }
    }

    private void validatePaymentInfo(OrderDto order, Integer amount) {
        if (order == null) {
            throw new PaymentException("주문 정보를 찾을 수 없습니다.");
        }
        if (!order.getTotalPrice().equals(amount)) {
            throw new PaymentException("결제 금액이 일치하지 않습니다.");
        }
        if (!"READY".equals(order.getOrderStatus())) {
            throw new PaymentException("잘못된 주문 상태입니다.");
        }
    }
}