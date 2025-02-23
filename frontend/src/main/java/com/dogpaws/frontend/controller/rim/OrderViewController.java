package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.dto.rim.*;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.TokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.PageImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderViewController {

    private final ApiRequestService apiRequestService;
    private final ObjectMapper objectMapper;




    /**
     * 주문 생성 페이지 : 선택된 장바구니 상품으로 주문서 작성
     */
    @GetMapping("/create")
    public String orderCreateView(@RequestParam(value = "cartItemIds", required = false) List<Long> cartItemIds,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
      log.info("orderCreateView........");

        // 선택된 상품이 없는 경우
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "주문할 상품을 선택해주세요.");
            return "redirect:/cart/list";
        }

        String token = TokenUtil.getTokenFromCookies(request);
        UserDto user = TokenUtil.verifyTokenAndSetSession(token, apiRequestService, session, request);

        try {
            // 선택된 장바구니 상품 정보 조회
            ApiResponse cartResponse = apiRequestService.fetchData(
                    "/api/cart/selected",
                    Map.of(
                            "username", user.getUsername(),
                            "cartItemIds", String.join(",", cartItemIds.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.toList()))
                    ),
                    false
            );

            log.info("cartResponse: {}", cartResponse);

            if (cartResponse.getStatus() != ApiResponse.ApiStatus.SUCCESS) {
                redirectAttributes.addFlashAttribute("error", "상품 정보를 불러오는데 실패했습니다.");
            }

            // API 응답 구조에 맞게 매핑
            Map<String, Object> responseBody = (Map<String, Object>) cartResponse.getBody();
            Map<String, Object> innerBody = (Map<String, Object>) responseBody.get("body");


            CartSummaryResponseDto selectedItems = objectMapper.convertValue(
                    innerBody,
                    CartSummaryResponseDto.class
            );

            // null check 추가
            if (selectedItems == null || selectedItems.getCartItems() == null || selectedItems.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "선택된 상품 정보가 없습니다.");
            }

            log.info("선택된 상품 수: {}", selectedItems.getCartItems().size());
            log.info("총 주문금액: {}", selectedItems.getTotalOrderPrice());

            model.addAttribute("selectedItems", selectedItems);
            model.addAttribute("user", user);

            return "rim/store/order_create";

        } catch (Exception e) {
            log.error("주문 페이지 로딩 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "주문 처리 중 오류가 발생했습니다.");
            return "redirect:/cart/list";
        }
    }

    @GetMapping("/success")
    public String orderSuccessView(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount,
            Model model,
            HttpServletRequest request) {

        log.info("결제 성공 처리 - paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);

        try {
            // 1. 결제 성공 API 호출
            ApiResponse paymentResponse = apiRequestService.fetchData(
                    "/api/order/success",
                    Map.of(
                            "paymentKey", paymentKey,
                            "qlId", orderId,
                            "amount", String.valueOf(amount)
                    ),
                    false
            );

            if (paymentResponse.getStatus() != ApiResponse.ApiStatus.SUCCESS) {
                throw new RuntimeException("결제 처리 실패: " + paymentResponse.getBody());
            }

            // 2. 주문 정보 조회 API 호출
            ApiResponse orderResponse = apiRequestService.fetchData(
                    "/api/order/" + orderId,
                    null,
                    false
            );

            if (orderResponse.getStatus() != ApiResponse.ApiStatus.SUCCESS) {
                throw new RuntimeException("주문 정보 조회 실패");
            }

            // 3. 주문 정보를 모델에 추가
            log.info("주문 정보 변환 시작 - responseBody: {}", orderResponse);
            Map<String, Object> responseBody = (Map<String, Object>) orderResponse.getBody();
            Map<String, Object> orderData = (Map<String, Object>) responseBody.get("body");
            log.info("주문 데이터 추출 - orderData: {}", orderData);

            OrderDto order = objectMapper.convertValue(orderData, OrderDto.class);
            log.info("주문 정보 변환 완료 - order: {}", order);
            model.addAttribute("order", order);

            return "rim/store/order_success";

        } catch (Exception e) {
            log.error("결제 완료 처리 중 오류 발생: {}", e.getMessage(), e);
            return "redirect:/error";
        }
    }


    @GetMapping("/list")
    public String orderListView(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session
    ) {
        try {
            UserDto user = (UserDto) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

            // 파라미터 맵 생성
            Map<String, String> params = new HashMap<>();
            params.put("page", String.valueOf(page));
            params.put("size", String.valueOf(size));

            // API 호출
            String path = "/api/order/user/" + user.getUsername();
            ApiResponse<?> response = apiRequestService.fetchData(path, params, false);

            log.info("orderListView response > {}", response);

            if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                // API 응답을 Map으로 변환
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                Map<String, Object> data = (Map<String, Object>) responseBody.get("body");

                // content를 OrderDto 리스트로 변환
                List<OrderDto> orders = objectMapper.convertValue(
                        data.get("content"),
                        new TypeReference<List<OrderDto>>() {}
                );

                // 페이징 정보 추출
                int totalPages = (int) data.get("totalPages");
                long totalElements = ((Number) data.get("totalElements")).longValue();

                model.addAttribute("orders", orders);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("totalElements", totalElements);

                return "rim/store/order_list";
            } else {
                throw new RuntimeException("주문 목록 조회 실패");
            }
        } catch (Exception e) {
            log.error("주문 목록 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("주문 목록 조회 실패", e);
        }
    }



    @GetMapping("/detail/{qlId}")
    public String orderDetailView(
            @PathVariable String qlId,
            Model model,
            HttpSession session
    ) {
        try {
            UserDto user = (UserDto) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

            // API 호출
            ApiResponse<?> response = apiRequestService.fetchData("/api/order/" + qlId, null, false);

            log.info("orderDetailView response > {}", response);

            if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                OrderDto order = objectMapper.convertValue(responseBody.get("body"), OrderDto.class);

                model.addAttribute("order", order);
                return "rim/store/order_detail";
            } else {
                throw new RuntimeException("주문 상세 조회 실패");
            }
        } catch (Exception e) {
            log.error("주문 상세 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("주문 상세 조회 실패", e);
        }
    }

    @GetMapping("/test/success")
    public String orderSuccessTestView(Model model) {
        // 테스트용 주문 상품 옵션 생성
        List<OrderItemOptionDto> options = List.of(
                OrderItemOptionDto.builder()
                        .optionId(23L)
                        .optionName("관절 강아지 사료 2kg (기본)")
                        .optionPrice(32500)
                        .quantity(1)
                        .build(),
                OrderItemOptionDto.builder()
                        .optionId(24L)
                        .optionName("관절 강아지 사료 5kg")
                        .optionPrice(72500)
                        .quantity(1)
                        .build()
        );

        // 테스트용 주문 상품 생성
        List<OrderItemDto> orderItems = List.of(
                OrderItemDto.builder()
                        .orderItemId(98L)
                        .productId(22L)
                        .productName("관절 강아지 사료")
                        .manufacturer("닥터독")
                        .imageUrl("http://localhost:2000/uploads/20250217113917045.jpg")
                        .amount(2)
                        .itemPrice(152500)
                        .options(options)
                        .build()
        );

        // 테스트용 주문 데이터 생성
        OrderDto testOrder = OrderDto.builder()
                .qlId("20250219-204621-7248")
                .orderId(85L)
                .username("test8")
                .totalPrice(350500)
                .orderStatus("PAID")
                .orderDate(LocalDateTime.now())
                .orderName("홍길동")
                .deliveryFee(3000)
                .orderPhone("01012345678")
                .shippingZipcode("06261")
                .shippingAddress1("서울 강남구 도곡로22길 5")
                .shippingAddress2("101동 1001호")
                .shippingMemo("부재시 경비실에 맡겨주세요")
                .receiverName("홍길동")
                .receiverPhone("01012345678")
                .orderItems(orderItems)
                .paymentKey("tgen_20250219204622ibq06")
                .build();

        model.addAttribute("order", testOrder);
        return "rim/store/order_success";
    }
}

