package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.LocationDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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


    public double[] getUserCoordinates(String username) {
        List<Object[]> userCoordinates = userRepository.findUserCoordinates(username);

        if (!userCoordinates.isEmpty()) {
            Object[] coordinates = userCoordinates.get(0); // 첫 번째 결과 가져오기

            System.out.println("coordinates length: " + coordinates.length);
            System.out.println("coordinates data: " + Arrays.toString(coordinates));

            // 좌표 길이 확인 및 NULL 체크
            if (coordinates.length < 2 || coordinates[0] == null || coordinates[1] == null) {
                throw new IllegalStateException("좌표 데이터가 부족합니다. username: " + username);
            }

            // 안전한 형 변환
            double latitude = ((Number) coordinates[0]).doubleValue();
            double longitude = ((Number) coordinates[1]).doubleValue();

            return new double[]{latitude, longitude};
        }

        return new double[]{37.5665, 126.9780}; // 기본값: 서울 좌표
    }

}
