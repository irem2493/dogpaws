package com.dogpaws.backend.entity.ajy;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_dog_personal")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DogPersonal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_personal_id", nullable = false)
    private Integer dogPersonalId;  // 강아지 성격 테이블 고유 넘버 (자동 증가)

    @Column(name = "dog_id", nullable = true)
    private Integer dogId;  // 강아지 ID (nullable)

    @Column(name = "dog_personal_gbn_cd", nullable = false, length = 20)
    private String dogPersonalGbnCd;  // 강아지 성격 구분 코드 (필수)
}
