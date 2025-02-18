package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductViewController {

    private final ApiRequestService apiRequestService;
    private static final String PRODUCT_API_PATH = "/api/products";

    @GetMapping("/{productId}")
    public String getProductDetail(@PathVariable Long productId, Model model) {
        String url = "/api/products/" + productId;
        log.info("상품 상세 정보 조회 요청: {}", url);

        ApiResponse response = apiRequestService.fetchData(url);

        // 중첩된 body에서 실제 상품 데이터 추출
        Map<String, Object> outerBody = (Map<String, Object>) response.getBody();
        Map<String, Object> innerBody = (Map<String, Object>) outerBody.get("body");

        model.addAttribute("product", innerBody);
        log.info("상품 상세 정보 조회 성공: productId={}", productId);

        return "rim/store/product_detail";
    }
}