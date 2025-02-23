package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.dto.rim.OrderDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderViewController {

    private final ApiRequestService apiRequestService;
    private final ObjectMapper objectMapper;

    /**
     * 관리자 주문 목록 조회 페이지
     */
    @GetMapping
    public String orderListView(
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            HttpSession session) {

        try {

            // API 요청 파라미터 설정
            Map<String, String> params = new HashMap<>();
            params.put("page", String.valueOf(page));
            params.put("size", String.valueOf(size));

            if (orderStatus != null) params.put("orderStatus", orderStatus);
            if (searchKeyword != null) params.put("searchKeyword", searchKeyword);
            if (startDate != null) params.put("startDate", startDate.toString());
            if (endDate != null) params.put("endDate", endDate.toString());

            // API 호출
            ApiResponse<?> response = apiRequestService.fetchData("/api/order", params, false);

            if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                List<OrderDto> orders = objectMapper.convertValue(
                        responseBody.get("body"),
                        new TypeReference<List<OrderDto>>() {}
                );

                model.addAttribute("orders", orders);
                model.addAttribute("currentPage", page);
                model.addAttribute("orderStatus", orderStatus);
                model.addAttribute("searchKeyword", searchKeyword);
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);

                return "rim/admin/admin_order_list";
            } else {
                throw new RuntimeException("주문 목록 조회 실패");
            }
        } catch (Exception e) {
            log.error("주문 목록 조회 중 오류 발생: {}", e.getMessage());
            return "error/500";
        }
    }
}
