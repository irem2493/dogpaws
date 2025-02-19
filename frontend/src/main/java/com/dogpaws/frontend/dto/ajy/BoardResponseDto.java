package com.dogpaws.frontend.dto.ajy;

import lombok.Data;

@Data
public class BoardResponseDto {
    private Integer boardId; // 게시판 글 ID (기본키, 자동 증가)

    private String username; // 사용자 ID (외래키)
    private String nickname;
    private String title;
    private String content;
    private String category;
    private int viewCount;
    private String createdAt;

}
