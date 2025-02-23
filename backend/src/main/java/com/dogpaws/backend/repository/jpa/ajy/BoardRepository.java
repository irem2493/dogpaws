package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Board;
<<<<<<< HEAD
import org.springframework.data.domain.Sort;
=======
>>>>>>> origin/REQ-68-관리자
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    List<Board> findByCategoryOrderByBoardIdDesc(String category);

    Board findByBoardId(Integer boardId);

    // 가장 높은 board_id 가져오기 (없으면 1 반환)
    @Query("SELECT COALESCE(MAX(b.boardId), 1) FROM Board b")
    Integer findMaxBoardId();

    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardId = :boardId")
    void increaseViewCount(@Param("boardId") Integer boardId);
<<<<<<< HEAD

    // 유저 아이디와 카테고리로 보드 리스트를 내림차순으로 게시판 아이디로 정렬
    List<Board> findByUsernameAndCategory(String userId, String category, Sort sort);
=======
>>>>>>> origin/REQ-68-관리자
}
