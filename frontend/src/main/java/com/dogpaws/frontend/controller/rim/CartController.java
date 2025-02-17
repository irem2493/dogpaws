package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.SessionUtil;
import com.dogpaws.frontend.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.URLEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cart")
@Controller
public class CartController {

    private final ApiRequestService apiRequestService;

    @GetMapping("/list")
    public String cartListView(Model model, HttpServletRequest request, HttpSession session) {
        String token = TokenUtil.getTokenFromCookies(request);

        UserDto user = TokenUtil.verifyTokenAndSetSession(token, apiRequestService, session, request);

        log.info("세션에 저장된 username = {}", user.getUsername());
        String url = "/api/cart";
        try {
            // 백엔드 API 호출
            ApiResponse response = apiRequestService.fetchData(url, Map.of("username", user.getUsername()), false);

            // response.getBody()가 Map 형태이므로 캐스팅
            if (response != null && response.getBody() instanceof Map) {
                Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();

                // 내부 status 확인
                if ("SUCCESS".equals(bodyMap.get("status"))) {
                    // body 필드에서 실제 장바구니 데이터 추출
                    model.addAttribute("body", bodyMap.get("body"));
                    log.info("장바구니 목록 조회 성공: {}", bodyMap.get("body"));
                } else {
                    model.addAttribute("error", "장바구니 목록을 불러오는데 실패했습니다.");
                    log.error("장바구니 목록 조회 실패: {}", bodyMap);
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "서버 오류가 발생했습니다.");
            log.error("장바구니 목록 조회 중 오류 발생", e);
        }

        return "rim/store/cart_list";
    }
}