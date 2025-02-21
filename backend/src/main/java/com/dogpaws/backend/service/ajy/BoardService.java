package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.BoardRequestDto;
import com.dogpaws.backend.dto.ajy.BoardResponseDto;
import com.dogpaws.backend.entity.ajy.Board;
import com.dogpaws.backend.entity.ajy.Comment;
import com.dogpaws.backend.repository.jpa.ajy.BoardRepository;
import com.dogpaws.backend.repository.jpa.ajy.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    //게시글 저장
    public void save(BoardRequestDto boardRequestDto) {
        Board board = Board.builder()
                .username(boardRequestDto.getUsername())
                .nickname(boardRequestDto.getNickname())
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .category(boardRequestDto.getCategory())
                .build();
        boardRepository.save(board);
    }

    //게시글 수정
    @Transactional
    public boolean edit(BoardRequestDto boardRequestDto){

        Board board = boardRepository.findByBoardId(boardRequestDto.getBoardId());

        log.info("board: " + board);

        if(board != null){
            board.setUsername(boardRequestDto.getUsername());
            board.setNickname(boardRequestDto.getNickname());
            board.setTitle(boardRequestDto.getTitle());
            board.setContent(boardRequestDto.getContent());
            board.setCategory(boardRequestDto.getCategory());
            board.setModifedAt(LocalDateTime.now());

            // 변경 사항 저장
            boardRepository.save(board);
            return true;
        }

        return false;
    }


    //게시글 조회
    public List<Board> getBoards() {
        return boardRepository.findAll();
    }

    //특정 게시글 조회
    public Board getBoardById(Integer board_id) {

        return boardRepository.findById(board_id).orElse(null);
    }

    //특정 게시글 삭제
    @Transactional
    public void deleteBoardById(Integer boardId) {
        boardRepository.deleteById(boardId);

        List<Comment> cList = commentRepository.findByBoardId(boardId);
        if(!cList.isEmpty()){
            for(Comment c : cList){
                commentRepository.deleteById(c.getCommentId());
            }
        }
    }

    //카테고리별 게시글 조회
    public List<BoardResponseDto> getBoardsByCategory(String category) {
        List<Board> bList = boardRepository.findByCategoryOrderByBoardIdDesc(category);

        List<BoardResponseDto> boardList = new ArrayList<>();
        if(!bList.isEmpty()){

            for (Board b : bList) {
                BoardResponseDto dto = new BoardResponseDto();
                dto.setBoardId(b.getBoardId());
                dto.setUsername(b.getUsername());
                dto.setNickname(b.getNickname());
                dto.setCategory(b.getCategory());
                dto.setCreatedAt(b.getCreatedAt().toString());
                dto.setTitle(b.getTitle());
                dto.setContent(b.getContent());
                dto.setViewCount(b.getViewCount());
                boardList.add(dto);
            }
        }
        return boardList;
    }

    //게시글 조회
    @Transactional
    public BoardResponseDto getBoardWithIncreaseView(Integer boardId) {
        boardRepository.increaseViewCount(boardId); // 🔥 조회수 증가

        Board board = boardRepository.findById(boardId).orElse(null);

        if(board != null){
            BoardResponseDto dto = new BoardResponseDto();
            dto.setBoardId(board.getBoardId());
            dto.setUsername(board.getUsername());
            dto.setNickname(board.getNickname());
            dto.setCategory(board.getCategory());
            dto.setCreatedAt(board.getCreatedAt().toString());
            dto.setTitle(board.getTitle());
            dto.setContent(board.getContent());
            dto.setViewCount(board.getViewCount());
            return dto;
        }

        return null;
    }
}
