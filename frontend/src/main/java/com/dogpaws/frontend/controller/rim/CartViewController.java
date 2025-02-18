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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

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


        log.info("길이 : {}", cartSummary.getCartItems().size());
        model.addAttribute("cartSummary", cartSummary);

        log.info("response : {}",response);
        log.info("response.status : {}",response.getStatus());
        log.info("response.body : {}",response.getBody());

        return "rim/store/cart_list";
    }
}