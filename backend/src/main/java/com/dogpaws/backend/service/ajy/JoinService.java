package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.DogRequestDto;
import com.dogpaws.backend.dto.ajy.JoinSessionDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.Dog;
import com.dogpaws.backend.entity.ajy.DogPersonal;
import com.dogpaws.backend.entity.ajy.DogPlay;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.repository.jpa.ajy.DogPersonalRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogPlayRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogRepository;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final DogPersonalRepository dogPersonalRepository;
    private final DogPlayRepository dogPlayRepository;

    public String  duplicateCheck(String username) {
        return userRepository.findByUsername(username) != null ? "중복됨" : "사용 가능";
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Transactional
    public void join(JoinSessionDto sessionData) {
        UserRequestDto userRequestDto = sessionData.getStep1Data();
        DogRequestDto dogRequestDto = sessionData.getStep2Data();

        User user = createUser(userRequestDto);
        userRepository.save(user);

        Dog dog = createDog(dogRequestDto);
        dogRepository.save(dog);

        Optional<Dog> dogId = dogRepository.findTopByOrderByDogIdDesc();
        if (dogId.isPresent()) {
            Integer maxDogId = dogId.get().getDogId();
            createDogPersonal(maxDogId, dogRequestDto.getSelectedPersonalities());
            createDogPlay(maxDogId, dogRequestDto.getSelectedPlays());
        }
    }

    private User createUser(UserRequestDto userRequestDto) {
        return User.builder()
                .username(userRequestDto.getUsername())
                .password(userRequestDto.getPassword())
                .nickname(userRequestDto.getNickname())
                .email(userRequestDto.getEmail())
                .postcode(userRequestDto.getPostcode())
                .address(userRequestDto.getAddress())
                .detailAddress(userRequestDto.getDetailAddress())
                .gender(userRequestDto.getGender())
                .ageGroup(userRequestDto.getAgeGroup())
                .role("ROLE_USER")
                .status('A')
                .build();
    }

    private Dog createDog(DogRequestDto dogRequestDto) {
        return Dog.builder()
                .username(dogRequestDto.getUsername())
                .dogName(dogRequestDto.getDogName())
                .breed(dogRequestDto.getBreed())
                .isMix(dogRequestDto.getIsMix())
                .birthYear(dogRequestDto.getBirthYear())
                .birthMonth(dogRequestDto.getBirthMonth())
                .gender(dogRequestDto.getGender())
                .isNeutered(dogRequestDto.getIsNeutered())
                .weight(dogRequestDto.getWeight())
                .walkStartTime(dogRequestDto.getWalkStartTime())
                .walkEndTime(dogRequestDto.getWalkEndTime())
                .walkDays(dogRequestDto.getWalkDays())
                .isMatingAvailable(dogRequestDto.getIsMatingAvailable())
                .dogIntro(dogRequestDto.getDogIntro())
                .profileUrl(dogRequestDto.getProfileUrl())
                .fileOldName(dogRequestDto.getFileOldName())
                .fileNewName(dogRequestDto.getFileNewName())
                .fileExt(dogRequestDto.getFileExt())
                .fileSize(dogRequestDto.getFileSize())
                .build();
    }

    private void createDogPersonal(Integer dogId, String selectedPersonalities) {

        if (selectedPersonalities == null || selectedPersonalities.isEmpty()) {
            return;  // 입력이 없으면 처리 중단
        }

        List<String> personalityCodes = Arrays.asList(selectedPersonalities.split(","));
        for (String personalityCode : personalityCodes) {
            if (!personalityCode.trim().isEmpty()) {  // 빈 값 방지
                dogPersonalRepository.save(DogPersonal.builder()
                        .dogId(dogId)
                        .dogPersonalGbnCd(personalityCode)
                        .build());
            }
        }
    }

    private void createDogPlay(Integer dogId, String selectPlays) {

        if (selectPlays == null || selectPlays.isEmpty()) {
            return;  // 입력이 없으면 처리 중단
        }

        List<String> playCodes = Arrays.asList(selectPlays.split(","));

        for(String playCode : playCodes) {
            if (!playCode.trim().isEmpty()) {  // 빈 값 방지
                dogPlayRepository.save(DogPlay.builder()
                        .dogId(dogId)
                        .dogPlayGbnCd(playCode)
                        .build());
            }
        }
    }
}
