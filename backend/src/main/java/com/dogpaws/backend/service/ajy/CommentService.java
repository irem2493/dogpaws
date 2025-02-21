package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.CommentRequestDto;
import com.dogpaws.backend.dto.ajy.CommentResponseDto;
import com.dogpaws.backend.entity.ajy.Board;
import com.dogpaws.backend.entity.ajy.Comment;
import com.dogpaws.backend.repository.jpa.ajy.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

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
           commentResponseDto.setBoardId(comment.getBoardId());
           commentResponseDto.setUsername(comment.getUsername());
           commentResponseDto.setNickname(comment.getNickname());
           commentResponseDto.setComment(comment.getComment());
           commentResponseDto.setCreatedAt(comment.getCreatedAt().toString());
           commentResponseDtos.add(commentResponseDto);
       }

       return commentResponseDtos;
   }

}
