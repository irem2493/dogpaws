package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.dto.rim.CartSummaryResponseDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

}