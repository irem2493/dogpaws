package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.DogRequestDto;
import com.dogpaws.backend.dto.ajy.JoinSessionDto;
import com.dogpaws.backend.dto.ajy.LocationDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.Dog;
import com.dogpaws.backend.entity.ajy.DogPersonal;
import com.dogpaws.backend.entity.ajy.DogPlay;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.repository.jpa.ajy.DogPersonalRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogPlayRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogRepository;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import com.dogpaws.backend.service.common.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JoinService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${front.file-dir}")
    private String fileDir;

    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final DogPersonalRepository dogPersonalRepository;
    private final DogPlayRepository dogPlayRepository;
    private final FileService fileService;
    private final LocationService locationService;

    public String duplicateCheck(String username) {
        return userRepository.findByUsername(username) != null ? "중복됨" : "사용 가능";
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }


    @Transactional
    public void join(JoinSessionDto sessionData) throws IOException {
        UserRequestDto userRequestDto = sessionData.getStep1Data();
        DogRequestDto dogRequestDto = sessionData.getStep2Data();
        Map<MultipartFile, String> fileTypeMap = sessionData.getStep3Data();

        User user = createUser(userRequestDto);

        String username = user.getUsername();

        userRepository.save(user);

        Dog dog = createDog(dogRequestDto);
        dogRepository.save(dog);

        System.out.println(dogRequestDto.getActivityImages());
        System.out.println(fileTypeMap);

        Optional<Dog> dogId = dogRepository.findTopByOrderByDogIdDesc();
        if (dogId.isPresent()) {
            Integer maxDogId = dogId.get().getDogId();
            createDogPersonal(maxDogId, dogRequestDto.getSelectedPersonalities());
            createDogPlay(maxDogId, dogRequestDto.getSelectedPlays());

            getActiveDogImage(maxDogId,username, dogRequestDto.getActivityImages());

            getMatching(maxDogId, username, fileTypeMap);
        }
    }

    //강아지 등록
    @Transactional
    public void dogRegister(DogRequestDto dogRequestDto) throws IOException {

        Dog dog = createDog(dogRequestDto);
        dogRepository.save(dog);

        System.out.println(dogRequestDto.getActivityImages());

        Optional<Dog> dogId = dogRepository.findTopByOrderByDogIdDesc();
        if (dogId.isPresent()) {
            Integer maxDogId = dogId.get().getDogId();
            createDogPersonal(maxDogId, dogRequestDto.getSelectedPersonalities());
            createDogPlay(maxDogId, dogRequestDto.getSelectedPlays());

            getActiveDogImage(maxDogId,dogRequestDto.getUsername(), dogRequestDto.getActivityImages());

            getMatching(maxDogId, dogRequestDto.getUsername(), dogRequestDto.getFileTypeMap());
        }
    }

    //강아지 업데이트
    @Transactional
    public void dogUpdate(DogRequestDto dogRequestDto) throws IOException {

        Dog dog = updateDog(dogRequestDto);
        dogRepository.save(dog);

        System.out.println(dogRequestDto.getActivityImages());

        dogPersonalRepository.deleteByDogId(dog.getDogId());
        dogPlayRepository.deleteByDogId(dog.getDogId());

        createDogPersonal(dog.getDogId(), dogRequestDto.getSelectedPersonalities());
        createDogPlay(dog.getDogId(), dogRequestDto.getSelectedPlays());

        getActiveDogImage(dog.getDogId(),dogRequestDto.getUsername(), dogRequestDto.getActivityImages());

        getMatching(dog.getDogId(), dogRequestDto.getUsername(), dogRequestDto.getFileTypeMap());

    }

    private User createUser(UserRequestDto userRequestDto) {

        LocationDto locationDto = locationService.getCoordinatesFromAddress(userRequestDto.getAddress());
        if (locationDto != null) {
            userRequestDto.setLatitude(locationDto.getLatitude());
            userRequestDto.setLongitude(locationDto.getLongitude());
        }


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
                .latitude(userRequestDto.getLatitude())
                .longitude(userRequestDto.getLongitude())
                .provider(Optional.ofNullable(userRequestDto.getProvider()).orElse(null)) // provider가 없으면 null
                .role("ROLE_USER")
                .status('A')
                .build();
    }

    private Dog createDog(DogRequestDto dogRequestDto) {
        if (dogRequestDto.getWalkTimeYn().equals("N")) {
            // 포맷 설정
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = LocalDateTime.now().format(formatter);
            dogRequestDto.setWalkStartTime(formattedTime);  // 포맷된 문자열로 저장
        }

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
                .walkTimeYn(dogRequestDto.getWalkTimeYn())
                .isMatingAvailable(dogRequestDto.getIsMatingAvailable())
                .dogIntro(dogRequestDto.getDogIntro())
                .profileUrl(dogRequestDto.getProfileUrl())
                .fileOldName(dogRequestDto.getFileOldName())
                .fileNewName(dogRequestDto.getFileNewName())
                .fileExt(dogRequestDto.getFileExt())
                .fileSize(dogRequestDto.getFileSize())
                .build();
    }

    private Dog updateDog(DogRequestDto dogRequestDto) {
        if (dogRequestDto.getWalkTimeYn().equals("N")) {
            // 포맷 설정
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = LocalDateTime.now().format(formatter);
            dogRequestDto.setWalkStartTime(formattedTime);  // 포맷된 문자열로 저장
        }

        return Dog.builder()
                .username(dogRequestDto.getUsername())
                .dogId(dogRequestDto.getDogId())
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
                .walkTimeYn(dogRequestDto.getWalkTimeYn())
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

        for (String playCode : playCodes) {
            if (!playCode.trim().isEmpty()) {  // 빈 값 방지
                dogPlayRepository.save(DogPlay.builder()
                        .dogId(dogId)
                        .dogPlayGbnCd(playCode)
                        .build());
            }
        }
    }

    private void getActiveDogImage(Integer dogId, String username, List<MultipartFile> filesList) throws IOException {
        if(filesList != null && !filesList.isEmpty()) {
            for(MultipartFile file : filesList) {
                System.out.println("파일 비었는가: " + file.isEmpty());  // 파일 상태 확인
                if(!file.isEmpty()){
                    System.out.println(file);
                    fileService.saveFile(file, "DP", dogId.toString(), username);
                }

            }
        }
    }

    private void getMatching(Integer dogId, String username, Map<MultipartFile, String> filesMap) throws IOException {
        if(filesMap != null && !filesMap.isEmpty()) {
            for (Map.Entry<MultipartFile, String> entry : filesMap.entrySet()) {
                MultipartFile file = entry.getKey();
                String fileCode = entry.getValue(); // 해당 파일의 구분 코드
                System.out.println("파일 비었는가: " + file.isEmpty());  // 파일 상태 확인
                if (!file.isEmpty()) {
                    fileService.deleteFileByDogIdAndFileCode(dogId, fileCode);

                    System.out.println(file);
                    fileService.saveFile(file, fileCode, dogId.toString(), username);
                }

            }
        }
    }

}
