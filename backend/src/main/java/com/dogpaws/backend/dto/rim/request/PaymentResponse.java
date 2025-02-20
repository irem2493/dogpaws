package com.dogpaws.backend.dto.rim.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 결제 응답 DTO
@Getter
@Setter
public class PaymentResponse {
    private String mId;                // 가맹점 ID
    private String version;            // Payment 객체 버전
    private String paymentKey;         // 결제 키
    private String orderId;            // 주문 ID
    private String orderName;          // 주문명
    private String currency;           // 통화
    private String method;             // 결제 수단
    private Long totalAmount;          // 총 결제 금액
    private String status;             // 결제 상태
    private LocalDateTime requestedAt; // 결제 요청 시각
    private LocalDateTime approvedAt;  // 결제 승인 시각
}