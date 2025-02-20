package com.dogpaws.backend.dto.rim;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatistics {
    private int totalOrderAmount;    // 총 주문 금액
    private int monthlyOrderCount;   // 월별 주문 건수
}
