package com.dogpaws.backend.dto.ajy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BoardResponseDto {

    @JsonProperty("boardId")
    private Integer boardId; // 게시판 글 ID (기본키, 자동 증가)

    private String username; // 사용자 ID (외래키)
    private String nickname;

    private String title;

    private Integer commentCount;

    private List<DogDto> dogList;

    private String content;
    private Integer viewCount;
    private String category;
    private String createdAt;

}
