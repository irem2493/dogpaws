package com.dogpaws.backend.dto.rim;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long orderId;
    
    private String qlId;
    
    private String paymentKey;
    
    private String username;        
    
    private Integer totalPrice;

    private LocalDateTime orderDate;

    private String orderName;
    
    private String orderPhone;

    private String shippingZipcode;
    
    private String shippingAddress1;
    
    private String shippingAddress2;
    
    private String shippingExtraAddress;
    
    private String shippingMemo;

    private String receiverName;
    
    private String receiverPhone;

    private OrderStatus orderStatus;

    private List<OrderItemDto> orderItems;
}
