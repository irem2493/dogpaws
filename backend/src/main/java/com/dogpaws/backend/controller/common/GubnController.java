package com.dogpaws.backend.controller.common;

import com.dogpaws.backend.dto.common.GubnDto;
import com.dogpaws.backend.service.common.GubnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/gubnCode")
    public GubnDto getGubn(@RequestParam("groupCode") String groupCode, @RequestParam("gubnCode") String gubnCode) {
        return gubnService.getGubn(groupCode, gubnCode);
    }
}
