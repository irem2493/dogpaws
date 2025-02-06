package com.dogpaws.backend.service.common;

import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("조회 요청 username: {}", username);

        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            log.info("User 조회 성공: {}", userEntity);
            return new CustomUserDetails(userEntity);
        }

        log.error("조회 실패: {}", username);
        throw new UsernameNotFoundException("해당 아이디의 유저가 없습니다: " + username);
    }
}
