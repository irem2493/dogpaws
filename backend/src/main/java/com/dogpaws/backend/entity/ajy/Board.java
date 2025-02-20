package com.dogpaws.backend.entity.ajy;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_boards") // 테이블명 지정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", updatable = false, nullable = false)
    private Integer boardId; // 게시판 글 ID (기본키, 자동 증가)

    @Column(name = "username", nullable = false, length = 255)
    private String username; // 사용자 ID (외래키)

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname; // 사용자 ID (외래키)

    @Column(name = "title", nullable = false, length = 255)
    private String title; // 제목

    @Lob
    @Column(name = "content", nullable = false)
    private String content; // 내용 (TEXT)

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "category", nullable = false, length = 1)
    private String category; // 카테고리 (문자열 저장: G, F, I, V, N)

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 일시 (기본값: 현재 시간)

    @Column(name = "modifed_at",columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modifedAt = LocalDateTime.now(); // 생성 일시 (기본값: 현재 시간)


    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if(viewCount == null){
            viewCount = 0;
        }
    }
}
