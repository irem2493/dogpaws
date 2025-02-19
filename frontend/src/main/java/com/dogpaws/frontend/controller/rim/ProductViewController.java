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
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping
    public String getProductList(@RequestParam(required = false) String category,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "12") int size,
                                 Model model) {
        try {
            // URL 파라미터 인코딩 수정
            String url = String.format("%s?page=%d&size=%d", PRODUCT_API_PATH, page, size);
            if (category != null && !category.isEmpty()) {
                url += "&category=" + URLEncoder.encode(category, StandardCharsets.UTF_8);
            }

            ApiResponse response = apiRequestService.fetchData(url);

            if (response != null && response.getBody() != null) {
                // response.getBody()를 직접 모델에 추가
                model.addAttribute("products", response.getBody());
                model.addAttribute("category", category);
                return "rim/store/product_list";
            } else {
                // 에러 처리
                log.error("상품 목록을 가져오는데 실패했습니다.");
                return "error/404";
            }
        } catch (Exception e) {
            log.error("상품 목록 조회 중 오류 발생: ", e);
            return "error/500";
        }
    }
}