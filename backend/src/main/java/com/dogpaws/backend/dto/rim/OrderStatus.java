package com.dogpaws.backend.dto.rim;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum OrderStatus {
    READY("결제대기"),
    PAID("결제완료"),
    CANCELLED("취소"),
    PREPARING("배송준비중"),
    SHIPPING("배송중"),
    DELIVERED("배송완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 진행 가능한 다음 상태 반환
    public List<OrderStatus> getNextStatuses() {
        return switch (this) {
            case READY -> Arrays.asList(PAID, CANCELLED);
            case PAID -> Arrays.asList(PREPARING, CANCELLED);
            case PREPARING -> Arrays.asList(SHIPPING);
            case SHIPPING -> Arrays.asList(DELIVERED);
            default -> Collections.emptyList();
        };
    }

    // 상태 변경이 가능한지 검증
    public boolean canChangeTo(OrderStatus nextStatus) {
        return getNextStatuses().contains(nextStatus);
    }
}
