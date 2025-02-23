package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.client.TossPaymentClient;
import com.dogpaws.backend.dto.rim.OrderDto;
import com.dogpaws.backend.dto.rim.OrderStatistics;
import com.dogpaws.backend.dto.rim.OrderStatus;
import com.dogpaws.backend.dto.rim.request.PaymentResponse;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 관리자용 주문 조회 API
     */
    @GetMapping
    public ApiResponse<?> getOrders(
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<OrderDto> orders = orderService.getOrdersForAdmin(
                orderStatus,
                searchKeyword,
                startDate,
                endDate,
                page,
                size
        );

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, orders);
    }

    @PutMapping("/{qlId}/tracking")
    public ApiResponse<?> updateTrackingNumber(
            @PathVariable String qlId,
            @RequestParam String trackingNumber) {
        orderService.updateTrackingNumber(qlId, trackingNumber);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "운송장 번호가 업데이트되었습니다.");
    }



    /**
     * 주문 생성 (결제 전)
     */
    @PostMapping
    public ApiResponse<?> createOrder(@RequestBody OrderDto request) {
        log.info("주문 생성 요청: {}", request);
        OrderDto order = orderService.createOrder(request);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, order);
    }


    /**
     * 결제 성공 처리
     */
    @GetMapping("/success")
    public ApiResponse<?> paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String qlId,
            @RequestParam Long amount) {

        log.info("결제 성공 처리: paymentKey={}, orderId={}, amount={}", paymentKey, qlId, amount);

        try {
            // 주문 상태 업데이트만 수행
            orderService.updateOrderStatus(qlId, OrderStatus.PAID, paymentKey);

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS,
                    Map.of("qlId", qlId));

        } catch (Exception e) {
            log.error("주문 상태 업데이트 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                    Map.of("message", "주문 처리 실패"));
        }
    }

    /**
     * 결제 실패 처리
     */
    @GetMapping("/fail")
    public ApiResponse<?> paymentFail(
            @RequestParam String orderId,
            @RequestParam String message,
            @RequestParam String code) {

        log.info("결제 실패 처리: orderId={}, message={}, code={}", orderId, message, code);

        // 결제 실패 시에는 주문 상태를 변경하지 않고 READY 상태 유지
        // 사용자가 다시 결제를 시도할 수 있음
        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR,
                Map.of("message", message,
                        "code", code,
                        "orderId", orderId));
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
     * 사용자의 주문 목록 조회 (페이징)
     */
    @GetMapping("/user/{username}")
    public ApiResponse<?> getUserOrders(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            // 전체 주문 수 조회
            int totalOrders = orderService.getUserOrdersCount(username);

            // 페이징된 주문 목록 조회
            List<OrderDto> orders = orderService.getUserOrdersWithPaging(username, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("content", orders);
            response.put("totalElements", totalOrders);
            response.put("totalPages", (int) Math.ceil((double) totalOrders / size));
            response.put("currentPage", page);
            response.put("size", size);

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, response);
        } catch (Exception e) {
            log.error("사용자 주문 목록 조회 실패: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, Map.of("message","주문 조회 실패."));
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