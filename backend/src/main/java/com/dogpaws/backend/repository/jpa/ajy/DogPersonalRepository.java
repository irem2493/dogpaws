package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Dog;
import com.dogpaws.backend.entity.ajy.DogPersonal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogPersonalRepository extends JpaRepository<DogPersonal, Integer> {

    // dogId을 통해 강아지 성격 정보를 모두 조회
    List<DogPersonal> findByDogId(Integer dogId);

}
