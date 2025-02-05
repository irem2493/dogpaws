package com.dogpaws.backend.controller.cys;

import com.dogpaws.backend.service.cys.DbtiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created on 2025-02-04 by 최윤서
 */
@RestController
@RequestMapping("/api")
public class DbtiController {

    @Autowired
    private DbtiService dbtiService;

    @PostMapping("/dbti")
    public ResponseEntity<String> receiveAnswers(@RequestBody Map<String, String> request) {
        String myType = request.get("myType");
        int dogId = 2;
        dbtiService.dogType(myType, dogId);
        System.out.println("선택된 답변 목록: " + myType);

        // 추가 처리 후 응답 반환
        return ResponseEntity.ok("응답을 잘 받았습니다!");
    }

}
