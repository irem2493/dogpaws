package com.dogpaws.frontend.dto.rim;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String qlId;                  // 주문 고유 번호
    private Long orderId;
    private String username;              // 주문자 아이디
    private Integer totalPrice;           // 총 주문 금액
    private Integer productPrice;         // 상품 금액
    private Integer deliveryFee;          // 배송비
    private String  orderStatus;           // 주문 상태
    private LocalDateTime orderDate;      // 주문 일시

    // 주문자 정보
    private String orderName;             // 주문자명
    private String orderPhone;            // 주문자 연락처

    // 배송 정보
    private String shippingZipcode;       // 배송지 우편번호
    private String shippingAddress1;      // 배송지 기본주소
    private String shippingAddress2;      // 배송지 상세주소
    private String shippingMemo;          // 배송 메모

    // 수령인 정보
    private String receiverName;          // 수령인 이름
    private String receiverPhone;         // 수령인 연락처

    // 주문 상품 정보
    private List<OrderItemDto> orderItems; // 주문 상품 목록

    // 결제 정보
    private String paymentKey;            // 결제 키
    private String paymentStatus;         // 결제 상태
    private LocalDateTime paidAt;         // 결제 완료 시간

<<<<<<< HEAD
    private String trackingNumber;

=======
>>>>>>> origin/REQ-68-관리자

    // 장바구니 상품 id 비우기용
    private List<Long> cartItemIds;
}

