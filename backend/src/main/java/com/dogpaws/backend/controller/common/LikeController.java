package com.dogpaws.backend.controller.common;


import com.dogpaws.backend.dto.common.LikeDto;
import com.dogpaws.backend.service.common.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 , 북마크
     *  username 사용자 이름
     *  likeCode 좋아요 코드 (예: P(교배), F(친구))
     *  likeId   강아지 ID
     */
    @PostMapping("/toggle")
    public String toggleLike(@RequestBody LikeDto likeDto) {
        log.info("toggleLike {}", likeDto);
        likeService.toggleLike(likeDto.getUsername(), likeDto.getLikeCode(), likeDto.getLikeId());
        return "성공";
    }
}
