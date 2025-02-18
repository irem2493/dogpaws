package com.dogpaws.backend.dto.rim.request;

import com.dogpaws.backend.dto.rim.CartItemDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {
    private String username;        // 사용자 ID
    private int totalPrice;        // 총 결제 금액

    // 주문자 정보
    private String orderName;    // 주문자명
    private String orderPhone;   // 주문자 연락처

    // 배송지 정보
    private String shippingZipcode;      // 우편번호
    private String shippingAddress1;      // 기본주소
    private String shippingAddress2;      // 상세주소
    private String shippingExtraAddress;  // 참고항목
    private String shippingMemo;          // 배송 메모

    // 수령인 정보
    private String receiverName;    // 받는사람 이름
    private String receiverPhone;   // 받는사람 연락처

    // 장바구니 상품 목록
    private List<CartItemDto> cartItems;  // 주문할 상품 목록
}