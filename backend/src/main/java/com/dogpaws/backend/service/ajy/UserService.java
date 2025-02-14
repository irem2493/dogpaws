package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.LocationDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LocationService locationService;
    private final PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmailAndProvider(String email, String provider) {
        return userRepository.findByEmailAndProvider(email, provider);
    }


    @Transactional
    public boolean updateUser(UserRequestDto userRequestDto) {
        User user = userRepository.findByUsername(userRequestDto.getUsername());

        if(user != null) {
            // 수정할 필드 값 변경
            String encryptedPassword = passwordEncoder.encode(userRequestDto.getPassword());

            user.setNickname(userRequestDto.getNickname());
            user.setPassword(encryptedPassword);
            user.setAgeGroup(userRequestDto.getAgeGroup());
            user.setGender(userRequestDto.getGender());
            user.setEmail(userRequestDto.getEmail());
            user.setPostcode(userRequestDto.getPostcode());
            user.setAddress(userRequestDto.getAddress());

            LocationDto locationDto = locationService.getCoordinatesFromAddress(userRequestDto.getAddress());
            if (locationDto != null) {
                user.setLatitude(locationDto.getLatitude());
                user.setLongitude(locationDto.getLongitude());
            }

            user.setDetailAddress(userRequestDto.getDetailAddress());
            user.setModifyDate(LocalDateTime.now());

            // 변경 사항 저장
            userRepository.save(user);
            return true;
        }

        return false;

    }

    @Transactional
    public boolean updateUserSocial(UserRequestDto userRequestDto) {
        User user = userRepository.findByUsername(userRequestDto.getUsername());

        if(user != null) {
            // 수정할 필드 값 변경
            user.setAddress(userRequestDto.getAddress());
            user.setDetailAddress(userRequestDto.getDetailAddress());
            user.setModifyDate(LocalDateTime.now());

            // 변경 사항 저장
            userRepository.save(user);
            return true;
        }

        return false;

    }

    @Transactional
    public boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username);

        if(user != null) {
            user.setRole("ROLE_NONE");
            user.setStatus('I');
            user.setModifyDate(LocalDateTime.now());
            return true;
        }
        return false;
    }

}
