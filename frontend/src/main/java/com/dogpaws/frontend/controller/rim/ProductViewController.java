package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.dto.rim.ProductListDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class ProductViewController {

    private final ApiRequestService apiRequestService;
    private static final String PRODUCT_API_PATH = "/api/products";
    private final ObjectMapper objectMapper;

<<<<<<< HEAD
    /**
     * 상품 상세페이지
     */
=======
>>>>>>> origin/REQ-68-관리자
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

<<<<<<< HEAD
    /**
     * 상품 메인페이지 
     */
=======
>>>>>>> origin/REQ-68-관리자
    @GetMapping
    public String getMainPage(Model model) {
        try {
            // 베스트 상품 조회
            ApiResponse response = apiRequestService.fetchData("/api/products/best");

            if (response != null) {
                Map<String, Object> outerBody = (Map<String, Object>) response.getBody();

                if ("SUCCESS".equals(outerBody.get("status"))) {
                    Map<String, Object> innerBody = (Map<String, Object>) outerBody.get("body");

                    // 각 카테고리별 베스트 상품을 모델에 추가
                    model.addAttribute("bestFoods", innerBody.get("bestFoods"));
                    model.addAttribute("bestSnacks", innerBody.get("bestSnacks"));
                    model.addAttribute("bestToys", innerBody.get("bestToys"));

                    log.info("베스트 상품 조회 성공: 사료={}, 간식={}, 장난감={} 개",
                            ((List<?>) innerBody.get("bestFoods")).size(),
                            ((List<?>) innerBody.get("bestSnacks")).size(),
                            ((List<?>) innerBody.get("bestToys")).size());
                } else {
                    log.error("API 응답 에러: {}", outerBody.get("message"));
                    model.addAttribute("error", "상품 정보를 불러오는데 실패했습니다");
                }
            }

            return "rim/store/store_main";
        } catch (Exception e) {
            log.error("메인 페이지 로딩 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "상품 정보를 불러오는데 실패했습니다");
            return "rim/store/store_main";
        }
    }

<<<<<<< HEAD
    /**
     * 상품조회 - 카테고리필터링
     */
=======
>>>>>>> origin/REQ-68-관리자
    @GetMapping("/category/{category}")
    public String getProductList(@PathVariable String category,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("page", String.valueOf(page));
            params.put("size", "12");
            params.put("main_category", category);

            log.info("카테고리 필터 적용: {}", category);

            ApiResponse response = apiRequestService.fetchData(PRODUCT_API_PATH, params, true);
            log.debug("API 응답: {}", response);


            Map<String, Object> outerBody = (Map<String, Object>) response.getBody();

            Map<String, Object> innerBody = (Map<String, Object>) outerBody.get("body");
            List<ProductListDto> products = objectMapper.convertValue(
                    innerBody.get("content"),
                    new TypeReference<List<ProductListDto>>() {}
            );

            // 모델에 데이터 추가
            model.addAttribute("products", products);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", innerBody.get("total_pages"));
            model.addAttribute("category", category);

            return "rim/store/product_list";

        } catch (Exception e) {
            log.error("상품 목록 조회 실패: {}", e.getMessage());
            model.addAttribute("error", "상품 목록을 불러오는데 실패했습니다");
            return "rim/store/product_list";
        }
    }
<<<<<<< HEAD

}
=======
}
>>>>>>> origin/REQ-68-관리자
