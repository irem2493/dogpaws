package com.dogpaws.backend.entity.ajy;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @Column(name = "username", length = 255, nullable = false, unique = true)
    private String username; // 사용자 ID (기본키)

    @Column(name = "provider", length = 255, nullable = true)
    private String provider; // 소셜 로그인 제공자 (ex: google, kakao, naver)

    @Column(name = "nickname", length = 255, nullable = false)
    private String nickname; // 사용자 닉네임

    @Column(name = "password", length = 255, nullable = true)
    private String password; // 비밀번호

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email; // 이메일 (유니크)

    @Column(name = "address", length = 255, nullable = false)
    private String address; // 사용자 주소

    @Column(name = "postcode", length = 255, nullable = false)
    private String postcode; // 사용자 주소

    @Column(name = "detail_address", length = 255, nullable = false)
    private String detailAddress; // 사용자 주소

    @Column(name = "age_group", length = 50)
    private String ageGroup; // 연령대 (예: "20대", "30대" 등)

    @Column(name = "gender", length = 1)
    private String  gender; // 성별 ('M' = 남성, 'W' = 여성)

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate = LocalDateTime.now(); // 가입 일시 (기본값: 현재 시간)

    @Column(name = "modify_date")
    private LocalDateTime modifyDate = LocalDateTime.now(); // 수정 일시 (기본값: 현재 시간)

    @Column(name = "status", length = 1, nullable = false)
    private Character status; // 회원 상태 (A:활성, I:비활성, B:제재됨)

    @Column(name = "role", length = 255, nullable = false)
    private String role; // 권한 (예: ROLE_USER, ROLE_ADMIN)

    @Column(name = "banned_date")
    private LocalDateTime bannedDate; // 제재된 날짜 (제재 상태일 때)

    @Column(name = "unbanned_date")
    private LocalDateTime unbannedDate; // 제재 해제된 날짜

    @PrePersist
    public void prePersist() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }
}
