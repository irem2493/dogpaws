package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.FilterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
@Slf4j
public class LikeListController {

    //좋아요 리스트 가져오기
    @GetMapping
    public FilterDto getLikeList(@RequestParam("dogId") int dogId,
                                 @RequestParam("likeType") String likeType) throws IOException {
        log.info("여기는 백 컨트롤러 getLikeList / dogId 값: {}", dogId);
        log.info("여기는 백 컨트롤러 getLikeList / likeType 값: {}", likeType);
        return null;
    }
}
