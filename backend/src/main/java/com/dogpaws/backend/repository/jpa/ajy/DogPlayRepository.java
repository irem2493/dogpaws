package com.dogpaws.backend.repository.jpa.ajy;


import com.dogpaws.backend.entity.ajy.DogPlay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogPlayRepository extends JpaRepository<DogPlay, Integer> {
    // dogId을 통해 강아지 선호놀이 정보를 모두 조회
    List<DogPlay> findByDogId(Integer dogId);
}
