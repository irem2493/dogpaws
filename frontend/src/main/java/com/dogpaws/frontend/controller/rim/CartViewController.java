package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.dto.rim.CartOptionResponseDto;
import com.dogpaws.frontend.dto.rim.CartSummaryResponseDto;
import com.dogpaws.frontend.dto.rim.ProductOptionDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.TokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cart")
@Controller
public class CartViewController {

    private final ApiRequestService apiRequestService;
    private final ObjectMapper objectMapper;

    @GetMapping("/list")
    public String cartListView(Model model, HttpServletRequest request, HttpSession session) {

        String token = TokenUtil.getTokenFromCookies(request);
        UserDto user = TokenUtil.verifyTokenAndSetSession(token, apiRequestService, session, request);

        log.info("세션에 저장된 username = {}", user.getUsername());

        ApiResponse response = apiRequestService.fetchData("/api/cart", Map.of("username", user.getUsername()), false);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        CartSummaryResponseDto cartSummary = objectMapper.convertValue(
                responseBody,
                CartSummaryResponseDto.class
        );

        // 각 상품의 전체 옵션 목록 조회
        if (cartSummary != null && !cartSummary.getCartItems().isEmpty()) {
            cartSummary.getCartItems().forEach(item -> {
                try {
                    ApiResponse optionsResponse = apiRequestService.fetchData(
                            "/api/products/" + item.getProductId() + "/options",
                            null,
                            false
                    );

                    if (optionsResponse.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                        // 응답 구조에 맞게 body에서 실제 옵션 목록 추출
                        Map<String, Object> responseMap = (Map<String, Object>) optionsResponse.getBody();
                        List<ProductOptionDto> allOptions = objectMapper.convertValue(
                                responseMap.get("body"),  // body 필드에서 실제 옵션 목록 가져오기
                                new TypeReference<List<ProductOptionDto>>() {}
                        );

                        // 현재 장바구니에 없는 옵션만 필터링
                        Set<Long> existingOptionIds = item.getCartOptions().stream()
                                .map(CartOptionResponseDto::getOptionId)
                                .collect(Collectors.toSet());

                        List<ProductOptionDto> availableOptions = allOptions.stream()
                                .filter(option -> !existingOptionIds.contains(option.getOptionId()))
                                .collect(Collectors.toList());

                        // 사용 가능한 추가 옵션 목록 설정
                        item.setAvailableOptions(availableOptions);

                        log.info("상품 ID: {}, 사용 가능한 옵션 수: {}",
                                item.getProductId(), availableOptions.size());
                    }
                } catch (Exception e) {
                    log.error("상품 옵션 조회 실패 - productId: {}, error: {}",
                            item.getProductId(), e.getMessage(), e);
                }
            });
        }


        log.info("길이 : {}", cartSummary.getCartItems().size());
        log.info("cartSummary : {}", cartSummary.toString());
        model.addAttribute("cartSummary", cartSummary);

        log.info("response : {}",response);
        log.info("response.status : {}",response.getStatus());
        log.info("response.body : {}",response.getBody());

        return "rim/store/cart_list";
    }

}