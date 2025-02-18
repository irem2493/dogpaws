package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.exception.PaymentException;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.CartService;
import com.dogpaws.backend.service.rim.OrderService;
import com.dogpaws.backend.service.rim.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ApiResponse<?> confirmPayment(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount) {
        try {
            log.info("결제 승인 요청: paymentKey={}, orderId={}, amount={}",
                    paymentKey, orderId, amount);

            paymentService.confirmPayment(paymentKey, orderId, amount);

            log.info("결제 승인 완료: orderId={}", orderId);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,
                    Map.of("message", "결제가 완료되었습니다."));

        } catch (PaymentException e) {
            log.error("결제 승인 실패: orderId={}, error={}", orderId, e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", e.getMessage()));
        }
    }
}