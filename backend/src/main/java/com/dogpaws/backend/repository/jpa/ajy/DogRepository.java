package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Integer> {
    Optional<Dog> findTopByOrderByDogIdDesc();

    // username을 통해 강아지 정보를 모두 조회
    List<Dog> findByUsername(String username);

    Dog findByDogId(Integer dogId);
}
