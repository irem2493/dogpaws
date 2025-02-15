package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.Dog;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Integer> {
    Optional<Dog> findTopByOrderByDogIdDesc();

    // username을 통해 강아지 정보를 모두 조회
    List<Dog> findByUsername(String username);

    List<Integer> findUserDogIdsByUsername(String username);

    Dog findByDogId(Integer dogId);

    @Query(value = "SELECT d.*, " +
            "(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(d.latitude)) " +
            "* COS(RADIANS(d.longitude) - RADIANS(:lng)) " +
            "+ SIN(RADIANS(:lat)) * SIN(RADIANS(d.latitude)))) AS distance " +
            "FROM dogs d " +
            "WHERE d.id NOT IN (:excludedDogIds) " +
            "HAVING distance <= :distance " +
            "ORDER BY distance", nativeQuery = true)
    List<Dog> findDogsNearby(@Param("lat") double lat,
                             @Param("lng") double lng,
                             @Param("distance") double distance,
                             @Param("excludedDogIds") List<Integer> excludedDogIds);
}
