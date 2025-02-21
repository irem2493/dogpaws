package com.dogpaws.backend.dto.ajy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentRequestDto {

    @JsonProperty("commentId")
    private Integer commentId;

    @JsonProperty("boardId")
    private Integer boardId;
    private String username;
    private String nickname;
    private String comment;
    private String category;
}
