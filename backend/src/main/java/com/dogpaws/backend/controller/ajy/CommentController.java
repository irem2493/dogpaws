package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.CommentRequestDto;
import com.dogpaws.backend.dto.ajy.CommentResponseDto;
import com.dogpaws.backend.entity.ajy.Board;
import com.dogpaws.backend.entity.ajy.Comment;
import com.dogpaws.backend.service.ajy.CommentService;
import com.dogpaws.frontend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //댓글 저장
    @PostMapping
    public ApiResponse<?> createComment(@RequestBody CommentRequestDto commentRequestDto) {
        log.info("CommentRequestDto: {}", commentRequestDto);
        commentService.save(commentRequestDto);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "댓글 등록 성공");
    }

    //댓글 리스트 조회
    @GetMapping("/comments/{boardId}/{category}")
    public ApiResponse<?> getComments(@PathVariable int boardId, @PathVariable String category) {
        List<CommentResponseDto> comments = commentService.findByBoardIdAndCategory(boardId, category);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, comments);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ApiResponse<?> deleteComment(@PathVariable Integer commentId) {
        Comment existComment = commentService.getCommentById(commentId);
        if (existComment != null) {
            commentService.deleteCommentById(commentId);
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "댓글 삭제 성공");
        }
        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "댓글 삭제 실패");
    }

}
