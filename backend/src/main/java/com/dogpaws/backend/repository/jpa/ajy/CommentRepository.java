package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByBoardIdAndCategory(Integer boardId, String category);

    List<Comment> findByBoardId(Integer boardId);
}
