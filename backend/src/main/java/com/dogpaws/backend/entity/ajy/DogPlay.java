package com.dogpaws.backend.entity.ajy;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_dog_play")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DogPlay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_play_id", nullable = false)
    private Integer dogPlayId;  // 강아지 선호놀이 고유 넘버 (자동 증가)

    @Column(name = "dog_id", nullable = true)
    private Integer dogId;  // 강아지 ID (nullable)

    @Column(name = "dog_play_gbn_cd", nullable = false, length = 20)
    private String dogPlayGbnCd;  // 강아지 선호놀이 구분 코드 (필수)
}

