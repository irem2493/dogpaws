package com.dogpaws.frontend.dto.rim;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {
    private String qlId;               // 주문 ID (자동생성)
    private String paymentKey;         // 결제 키
    private String username;           // 사용자 ID
    private int totalPrice;            // 총 결제 금액
    private String orderName;          // 주문자명
    private String orderPhone;         // 주문자 연락처
    private String shippingMemo;       // 배송 메모
    private String shippingZipcode;    // 우편번호
    private String shippingAddress1;   // 기본주소
    private String shippingAddress2;   // 상세주소
    private String receiverName;       // 받는사람 이름
    private String receiverPhone;      // 받는사람 연락처
    private String orderStatus;        // 주문상태 (기본값: READY)
    private List<OrderItemDto> orderItems;  // 주문 상품 목록
}
