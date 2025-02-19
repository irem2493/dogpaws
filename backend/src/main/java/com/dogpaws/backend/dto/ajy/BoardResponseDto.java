package com.dogpaws.backend.dto.ajy;

import lombok.Data;

@Data
public class BoardResponseDto {
    private Integer boardId; // 게시판 글 ID (기본키, 자동 증가)

    private String username; // 사용자 ID (외래키)
    private String nickname;

    private String title;
    private String content;
    private Integer viewCount;
    private String category;
    private String createdAt;

}
