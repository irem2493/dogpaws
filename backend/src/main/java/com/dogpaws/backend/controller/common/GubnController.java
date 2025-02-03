package com.dogpaws.backend.controller.common;

import com.dogpaws.backend.dto.common.GubnDto;
import com.dogpaws.backend.service.common.GubnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 01-29 (작성자: 안제연)
 * 이 클래스는 구분 RestController입니다.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gubn")
public class GubnController {

    private final GubnService gubnService;

    @GetMapping("/{groupCode}")
    public List<GubnDto> getGubnList(@PathVariable String groupCode) {
        return gubnService.getGubnList(groupCode);
    }
}
