package com.dogpaws.frontend.controller.rim;

import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/admin/product")
@Controller
@Slf4j
public class AdminProductViewController {


    private final ApiRequestService apiRequestService;
    private final ObjectMapper objectMapper;


    @GetMapping("/regist")
    public String productRegistView() {
        return "rim/admin/admin_product_register";
    }

    @GetMapping("/manage")
    public String productManageView(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            // API 호출을 위한 파라미터 설정
            Map<String, String> params = new HashMap<>();
            params.put("mainCategory", category);
            params.put("status", status);
            params.put("sortBy", sortBy);
            params.put("searchKeyword", keyword);
            params.put("page", String.valueOf(page));
            params.put("size", String.valueOf(size));

            // API 호출
            ApiResponse response = apiRequestService.fetchData("/api/admin/products/manage", params, false);
            log.info("API 요청 파라미터: {}", params);

            log.info(objectMapper.writeValueAsString(response));

            if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                model.addAttribute("productList", response.getBody());
                log.info("상품 목록 조회 성공: {}", response.getBody());
            } else {
                log.error("상품 목록 조회 실패: {}", response.getBody());
            }

            // 카테고리 정보
            Map<String, String> mainCategories = new HashMap<>();
            mainCategories.put("F", "사료");
            mainCategories.put("N", "간식");
            mainCategories.put("T", "장난감");
            model.addAttribute("mainCategories", mainCategories);

            // 상태 정보
            Map<String, String> statuses = new HashMap<>();
            statuses.put("O", "판매중");
            statuses.put("S", "품절");
            statuses.put("D", "판매중지");
            model.addAttribute("statuses", statuses);

            // 정렬 옵션
            Map<String, String> sortOptions = new HashMap<>();
            sortOptions.put("stock_asc", "재고 적은순");
            sortOptions.put("stock_desc", "재고 많은순");
            model.addAttribute("sortOptions", sortOptions);

        } catch (Exception e) {
            log.error("상품 관리 페이지 로딩 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "데이터 로딩 중 오류가 발생했습니다.");
        }

        return "rim/admin/admin_product_manage";
    }
}