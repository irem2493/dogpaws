package com.dogpaws.backend.entity.ajy;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id", nullable = false)
    private Integer tokenId;

    @Column(nullable = false, unique = true)
    private String username;  // 사용자 아이디

    @Column(nullable = false)
    private String refreshToken;  // Refresh Token

    @Column(name="updated_Date", nullable = false)
    private LocalDateTime updatedDate;  // 토큰 갱신 시간

    @PrePersist
    public void prePersist() {
        if (updatedDate == null) {
            updatedDate = LocalDateTime.now();
        }
    }
}
