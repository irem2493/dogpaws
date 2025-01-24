package com.dogpaws.backend.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.backend.entity.Company;
import org.green.backend.entity.User;
import org.green.backend.repository.jpa.hws.CompanyRepository;
import org.green.backend.repository.jpa.hws.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("조회 요청 username: {}", username);

        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            log.info("User 조회 성공: {}", userEntity);
            return new CustomUserDetails(userEntity);
        }

        Company companyEntity = companyRepository.findByUsername(username);
        if (companyEntity != null) {
            log.info("Company 조회 성공: {}", companyEntity);
            return new CustomUserDetails(companyEntity);
        }

        log.error("조회 실패: {}", username);
        throw new UsernameNotFoundException("해당 아이디의 유저 또는 기업이 없습니다: " + username);
    }
}