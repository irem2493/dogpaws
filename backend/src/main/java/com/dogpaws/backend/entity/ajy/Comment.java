package com.dogpaws.backend.entity.ajy;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId; // 댓글 ID

    @Column(name = "board_id", nullable = false)
    private Integer boardId; // 게시글 ID

    @Column(name = "username", nullable = false, length = 255)
    private String username; // 사용자 ID

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname = ""; // 사용자 닉네임

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment; // 댓글 내용

    @Column(name = "category", nullable = false, length = 1)
    private String category; // 카테고리 (문자열 저장: G, F, I, V, N)

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime createdAt; // 댓글 작성 일시

    @Column(name = "modifed_at")
    private LocalDateTime modifiedAt; // 댓글 수정 일시

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

}

