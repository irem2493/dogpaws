package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.DeclarationDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.hyepin.DeclarationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/declaration")
@RequiredArgsConstructor
@Slf4j
public class DeclarationController {

    private final DeclarationService declarationService;

    //신고 등록
    @PostMapping
    public ApiResponse<String> registDeclaration(@ModelAttribute DeclarationDto declarationDto) throws IOException {
        log.info("여기는 백 컨트롤러 registDeclaration / DeclarationDto 값: {}", declarationDto);
        int result = declarationService.insertDeclaration(declarationDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "신고 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "신고 실패");
        }
    }

}