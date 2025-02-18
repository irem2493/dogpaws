package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.rim.OrderDto;
import com.dogpaws.backend.dto.rim.OrderStatistics;
import com.dogpaws.backend.dto.rim.request.OrderCreateRequest;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 주문 생성
     */
    @PostMapping
    public ApiResponse<?> createOrder(@RequestBody OrderCreateRequest request) {
        log.info("주문 생성 요청: {}", request);
        OrderDto order = orderService.createOrder(request);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,order);
    }
    /**
     * 주문 조회
     */
    @GetMapping("/{qlId}")
    public ApiResponse<?> getOrder(@PathVariable String qlId) {
        try {
            OrderDto order = orderService.getOrder(qlId);
            if (order == null) {
                return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, Map.of("message","주문 정보가 없습니다."));
            }
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, order);
        } catch (Exception e) {
            log.error("주문 조회 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, Map.of("message","주문 조회 실패."));
        }
    }

    /**
     * 사용자의 주문 목록 조회
     */
    @GetMapping("/user/{username}")
    public ApiResponse<?> getUserOrders(@PathVariable String username) {
        try {
            List<OrderDto> orders = orderService.getUserOrders(username);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, orders);
        } catch (Exception e) {
            log.error("사용자 주문 목록 조회 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,  Map.of("message","주문 조회 실패."));
        }
    }

    /**
     * 주문 상태 업데이트
     */
    @PutMapping("/{qlId}/status")
    public ApiResponse<?> updateOrderStatus(
            @PathVariable String qlId,
            @RequestParam String status) {
        try {
            orderService.updateOrderStatus(qlId, status);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,
                    Map.of("message", "주문 상태가 업데이트되었습니다."));
        } catch (IllegalArgumentException e) {
            log.error("주문 상태 업데이트 실패: {}", e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", e.getMessage()));
        }
    }

    /**
     * 결제 완료 처리
     */
    @PutMapping("/{qlId}/payment")
    public ApiResponse<?> completePayment(
            @PathVariable String qlId,
            @RequestParam String paymentKey) {
        try {
            orderService.completePayment(qlId, paymentKey);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,
                    Map.of("message", "결제가 완료되었습니다."));
        } catch (Exception e) {
            log.error("결제 완료 처리 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "결제 처리 실패"));
        }
    }

    /**
     * 주문 취소
     */
    @PostMapping("/{qlId}/cancel")
    public ApiResponse<?> cancelOrder(
            @PathVariable String qlId,
            @RequestParam(required = false) String reason) {
        try {
            orderService.cancelOrder(qlId, reason);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,
                    Map.of("message", "주문이 취소되었습니다."));
        } catch (IllegalStateException e) {
            log.error("주문 취소 실패: {}", e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", e.getMessage()));
        }
    }

    /**
     * 사용자의 주문 통계
     */
    @GetMapping("/user/{username}/statistics")
    public ApiResponse<?> getUserOrderStatistics(@PathVariable String username) {
        try {
            OrderStatistics statistics = orderService.getUserOrderStatistics(username);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, statistics);
        } catch (Exception e) {
            log.error("주문 통계 조회 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "주문 통계 조회 실패"));
        }
    }
}
