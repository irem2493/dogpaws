package com.dogpaws.backend.dto.rim.request;

import com.dogpaws.backend.dto.rim.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// 주문 결과 응답 DTO (결제 위젯용)
@Getter
@Setter
@ToString
public class OrderResponseDto {
    private String qlId;            // 주문번호
    private int totalPrice;         // 결제 금액
    private String ordererName;     // 주문자명
    private OrderStatus orderStatus; // 주문 상태
    private String paymentKey;      // 결제 키 (결제 완료 시)
    private LocalDateTime orderDate; // 주문 일시
}