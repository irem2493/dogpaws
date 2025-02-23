package com.dogpaws.backend.dto.ajy;

import lombok.Data;

import java.util.List;

@Data
public class CommentResponseDto {

    private Integer commentId;
    private Integer boardId;
    private String username;
    private String nickname;
    private String comment;
    private String createdAt;

    private List<DogDto> dogList;
}
