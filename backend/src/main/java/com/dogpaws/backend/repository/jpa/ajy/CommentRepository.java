package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByBoardIdAndCategory(Integer boardId, String category, Sort sort);


    List<Comment> findByBoardId(Integer boardId);

    Comment findByCommentId(Integer commentId);


    @Modifying
    @Query("UPDATE Comment c SET c.comment = :comment, c.modifiedAt = CURRENT_TIMESTAMP WHERE c.commentId = :commentId")
    void updateCommentContent(@Param("commentId") Integer commentId, @Param("comment") String comment);


}
