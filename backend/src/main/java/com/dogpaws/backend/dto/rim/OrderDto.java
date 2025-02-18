package com.dogpaws.backend.dto.rim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDto {
    private Long orderId;           // 주문 ID
    private String qlId;            // 주문번호
    private String paymentKey;      // 결제 키
    private String username;        // 사용자 ID
    private int totalPrice;         // 총 결제 금액
    private LocalDateTime orderDate; // 주문 일시

    // 주문자 정보
    private String ordererName;     // 주문자명
    private String ordererPhone;    // 주문자 연락처

    // 배송지 정보
    private String shippingZipcode;       // 우편번호
    private String shippingAddress1;       // 기본주소
    private String shippingAddress2;       // 상세주소
    private String shippingExtraAddress;   // 참고항목
    private String shippingMemo;           // 배송 메모

    // 수령인 정보
    private String receiverName;     // 받는사람 이름
    private String receiverPhone;    // 받는사람 연락처

    // 주문 상태
    private OrderStatus orderStatus; // 주문/배송 상태

    // 주문 상품 목록
    private List<OrderItemDto> orderItems;
}
