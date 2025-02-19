package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.BoardRequestDto;
import com.dogpaws.backend.dto.ajy.BoardResponseDto;
import com.dogpaws.backend.entity.ajy.Board;
import com.dogpaws.backend.service.ajy.BoardService;
import com.dogpaws.frontend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //게시글 저장
    @PostMapping
    public ApiResponse<?> createBoard(@RequestBody  BoardRequestDto boardRequestDto) {
        log.info("BoardRequestDto: {}", boardRequestDto);
        boardService.save(boardRequestDto);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "게시글 등록 성공");
    }

    //카테고리 별 게시글 목록 조회
    @GetMapping("/boards/{category}")
    public ApiResponse<?> getCategoryBoards(@PathVariable("category")String category) {
       return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, boardService.getBoardsByCategory(category));

    }

    //특정 게시글 조회
    @GetMapping("/{boardId}")
    public ApiResponse<?> getBoard(@PathVariable("boardId") Integer boardId) {
        BoardResponseDto board = boardService.getBoardWithIncreaseView(boardId);
        if (board != null) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, board);
        }
        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "조회 실패");
    }

    //특정 게시물 수정
    @PutMapping("/{boardId}")
    public ApiResponse<?> updateBoard(@PathVariable("boardId") Integer boardId, @RequestBody BoardRequestDto board) {
        Board existBoard = boardService.getBoardById(boardId);
        if (existBoard != null) {
            board.setBoardId(boardId);
            boardService.save(board);
        }
        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "수정 실패");
    }

    //특정 게시물 삭제
    @DeleteMapping("/{boardId}")
    public ApiResponse<?> deleteBoard(@PathVariable("boardId") Integer boardId) {
        Board existBoard = boardService.getBoardById(boardId);
        if (existBoard != null) {
            boardService.deleteBoardById(boardId);
        }
        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "삭제 실패");
    }



}
