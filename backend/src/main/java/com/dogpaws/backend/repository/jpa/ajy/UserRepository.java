package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    User findByEmailAndProvider(String email, String provider);

    @Query("SELECT u.latitude, u.longitude FROM User u WHERE u.username = :username")
    List<Object[]> findUserCoordinates(@Param("username") String username);
}
