package com.dogpaws.backend.dto.ajy;

import lombok.Data;

@Data
public class BoardRequestDto {
    private int boardId;
    private String username; // 사용자 ID (외래키)
    private String nickname;
    private String title;
    private String content;
    private String category;
    private int viewCount;
}
