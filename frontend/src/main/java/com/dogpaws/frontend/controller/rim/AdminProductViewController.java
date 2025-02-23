package com.dogpaws.frontend.controller.rim;

<<<<<<< HEAD
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
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
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/admin/products")
@Controller
@Slf4j
public class AdminProductViewController {

    private final ApiRequestService apiRequestService;
    private final ObjectMapper objectMapper;

    /**
     * 관리자 상품 등록 페이지
     */
=======
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin/product")
@Controller
public class AdminProductViewController {
>>>>>>> origin/REQ-68-관리자
    @GetMapping("/regist")
    public String productRegistView() {
        return "rim/admin/admin_product_register";
    }
<<<<<<< HEAD

    /**
     * 관리자 상품 조회 페이지 ( 간단한 상태관리, 수정, 삭제 )
     */
    @GetMapping("/manage")
    public String productManageView(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword,
            Model model) {
        try {
            // API 호출을 위한 파라미터 설정
            Map<String, String> params = new HashMap<>();
            params.put("mainCategory", category);
            params.put("status", status);
            params.put("sortBy", sortBy);
            params.put("searchKeyword", keyword);

            log.info("상품 목록 조회 요청 - 카테고리: {}, 상태: {}, 정렬: {}, 검색어: {}", 
                    category, status, sortBy, keyword);

            // API 호출
            ApiResponse response = apiRequestService.fetchData("/api/admin/products/manage", params, false);

            if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                model.addAttribute("productList",  responseBody.get("body"));
                log.debug("상품 목록 조회 성공 - 데이터: {}", responseBody.get("body"));
            } else {
                log.error("상품 목록 조회 실패 - 응답: {}", response);
                model.addAttribute("error", "상품 목록을 불러오는데 실패했습니다.");
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
            log.error("상품 관리 페이지 로딩 중 오류 발생", e);
            model.addAttribute("error", "데이터 로딩 중 오류가 발생했습니다.");
        }

        return "rim/admin/admin_product_manage";
    }

    /**
     * 상품 수정 페이지
     */
    @GetMapping("/edit/{productId}")
    public String productEditView(@PathVariable Long productId, Model model) {
        log.info("상품 수정 페이지 요청 - 상품 ID: {}", productId);

        try {
            // 상품 정보 조회
            ApiResponse response = apiRequestService.fetchData("/api/admin/products/" + productId, null, false);

            if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                Map<String, Object> body = (Map<String, Object>) responseBody.get("body");

                // 실제 상품 데이터를 모델에 추가
                model.addAttribute("product", body);
                log.debug("상품 정보 조회 성공 - 데이터: {}", body);

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

            } else {
                log.error("상품 정보 조회 실패 - 응답: {}", response);
                return "redirect:/admin/products/manage";
            }

        } catch (Exception e) {
            log.error("상품 수정 페이지 로딩 중 오류 발생", e);
            return "redirect:/admin/products/manage";
        }

        return "rim/admin/admin_product_edit";
    }
=======
>>>>>>> origin/REQ-68-관리자
}