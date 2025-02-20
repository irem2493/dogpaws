package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Board;
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
}
