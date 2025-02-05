package com.dogpaws.backend.repository.jpa.ajy;

import com.dogpaws.backend.entity.ajy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
