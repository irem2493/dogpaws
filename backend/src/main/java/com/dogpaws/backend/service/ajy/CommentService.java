package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.BoardRequestDto;
import com.dogpaws.backend.dto.ajy.CommentRequestDto;
import com.dogpaws.backend.dto.ajy.CommentResponseDto;
import com.dogpaws.backend.entity.ajy.Board;
import com.dogpaws.backend.entity.ajy.Comment;
import com.dogpaws.backend.repository.jpa.ajy.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;

   public void save(CommentRequestDto commentRequestDto) {
       log.info("comment : {}", commentRequestDto);
      Comment comment = Comment.builder()
              .boardId(commentRequestDto.getBoardId())
              .username(commentRequestDto.getUsername())
              .nickname(commentRequestDto.getNickname())
              .comment(commentRequestDto.getComment())
              .category(commentRequestDto.getCategory())
              .createdAt(LocalDateTime.now())
              .build();
      commentRepository.save(comment);
   }

   public List<CommentResponseDto> findByBoardIdAndCategory(Integer boardId, String category) {
       List<Comment> comments = commentRepository.findByBoardIdAndCategory(boardId, category);
       List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
       for(Comment comment : comments) {
           log.info("comment : {}", comment);
           CommentResponseDto commentResponseDto = new CommentResponseDto();
           commentResponseDto.setCommentId(comment.getCommentId());
           commentResponseDto.setBoardId(comment.getBoardId());
           commentResponseDto.setUsername(comment.getUsername());
           commentResponseDto.setNickname(comment.getNickname());
           commentResponseDto.setComment(comment.getComment());
           commentResponseDto.setCreatedAt(comment.getCreatedAt().toString());
           commentResponseDtos.add(commentResponseDto);
       }

       return commentResponseDtos;
   }

   //특정 게시글 조회
    public Comment getCommentById(Integer commentId) {
       return commentRepository.findByCommentId(commentId);
    }

    //특정 게시글 삭제
    public void deleteCommentById(Integer commentId) {
       commentRepository.deleteById(commentId);
    }

    //게시글 번호에 해당되는 댓글 반환
    public List<Comment> getCommentsByBoardId(Integer boardId) {
       return commentRepository.findByBoardId(boardId);
    }

    //댓글 수정
    @Transactional
    public boolean edit(Integer commentId, String updateText) {

        Comment comment = commentRepository.findByCommentId(commentId);

        log.info("comment: " + comment);

        if(comment != null){
            // 변경 사항 저장
            commentRepository.updateCommentContent(commentId, updateText);
            return true;
        }

        return false;
    }

}
