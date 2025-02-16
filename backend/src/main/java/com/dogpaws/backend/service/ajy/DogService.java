package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.DogDto;
import com.dogpaws.backend.dto.ajy.DogResponseDto;
import com.dogpaws.backend.dto.common.FileDto;
import com.dogpaws.backend.entity.File;
import com.dogpaws.backend.entity.ajy.Dog;
import com.dogpaws.backend.entity.ajy.DogPersonal;
import com.dogpaws.backend.entity.ajy.DogPlay;
import com.dogpaws.backend.repository.jpa.ajy.DogPersonalRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogPlayRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogRepository;
import com.dogpaws.backend.service.common.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogService {
    private final UserService userService;
    private final DogRepository dogRepository;
    private final DogPersonalRepository dogPersonalRepository;
    private final DogPlayRepository dogPlayRepository;
    private final FileService fileService;

    public List<DogDto> getDogs(String username){
        List<DogDto> dogDtoList = new ArrayList<>();
        List<Dog> dogList = dogRepository.findByUsername(username);
        for (Dog d : dogList) {
            DogDto dogDto = new DogDto();
            dogDto.setDogId(d.getDogId());
            dogDto.setDogName(d.getDogName());
            dogDto.setDogName(d.getDogName());
            dogDto.setProfileUrl(d.getProfileUrl());
            dogDtoList.add(dogDto);
        }
        return dogDtoList;
    }

    public List<DogResponseDto> getDogList(String username) {
        List<DogResponseDto> dogResponseDtoList = new ArrayList<>();
        List<Integer> dogIdList = new ArrayList<>();

        List<Dog> dogList = dogRepository.findByUsername(username);

        for (Dog d : dogList) {
            dogIdList.add(d.getDogId());
        }

        for (Dog d : dogList) {
            DogResponseDto dogDto = new DogResponseDto();
            dogDto.setUsername(d.getUsername());
            dogDto.setDogId(d.getDogId());
            dogDto.setBreed(d.getBreed());
            dogDto.setIsMix(d.getIsMix());
            dogDto.setDogName(d.getDogName());
            dogDto.setBirthYear(d.getBirthYear());
            dogDto.setBirthMonth(d.getBirthMonth());
            dogDto.setGender(d.getGender());
            dogDto.setIsNeutered(d.getIsNeutered());
            dogDto.setWeight(d.getWeight());
            dogDto.setWalkDays(d.getWalkDays());

            dogDto.setWalkTimeYn(d.getWalkTimeYn());
            if(d.getWalkTimeYn().equals("N")) {
                dogDto.setWalkStartTime(d.getWalkStartTime());
                dogDto.setWalkEndTime(d.getWalkEndTime());
            }

            dogDto.setDogIntro(d.getDogIntro());
            dogDto.setPersonalityType(d.getPersonalityType());
            dogDto.setProfileUrl(d.getProfileUrl());

            // ✅ 각 강아지별 성격 리스트 조회
            List<DogPersonal> dogPersonalList = dogPersonalRepository.findByDogId(d.getDogId());
            if (!dogPersonalList.isEmpty()) {
                String selectedPersonalities = dogPersonalList.stream()
                        .map(DogPersonal::getDogPersonalGbnCd) // 코드 값 추출
                        .collect(Collectors.joining(",")); // 문자열로 변환
                dogDto.setSelectedPersonalities(selectedPersonalities);
            }

            // ✅ 각 강아지별 선호하는 놀이 리스트 조회
            List<DogPlay> dogPlayList = dogPlayRepository.findByDogId(d.getDogId());
            if (!dogPlayList.isEmpty()) {
                String selectedPlays = dogPlayList.stream()
                        .map(DogPlay::getDogPlayGbnCd) // 코드 값 추출
                        .collect(Collectors.joining(",")); // 문자열로 변환
                dogDto.setSelectedPlays(selectedPlays);
            }
            System.out.println(dogDto);
            dogResponseDtoList.add(dogDto);
        }

        return dogResponseDtoList;
    }

    public DogResponseDto getDog(Integer dogId) {
        Dog dog = dogRepository.findByDogId(dogId);

        if (dog != null) {
            DogResponseDto dogDto = new DogResponseDto();
            dogDto.setUsername(dog.getUsername());
            dogDto.setDogId(dog.getDogId());
            dogDto.setBreed(dog.getBreed());
            dogDto.setIsMix(dog.getIsMix());
            dogDto.setDogName(dog.getDogName());
            dogDto.setBirthYear(dog.getBirthYear());
            dogDto.setBirthMonth(dog.getBirthMonth());
            dogDto.setGender(dog.getGender());
            dogDto.setIsNeutered(dog.getIsNeutered());
            dogDto.setWeight(dog.getWeight());
            dogDto.setWalkDays(dog.getWalkDays());

            dogDto.setWalkTimeYn(dog.getWalkTimeYn());
            if (dog.getWalkTimeYn().equals("N")) {
                dogDto.setWalkStartTime(dog.getWalkStartTime());
                dogDto.setWalkEndTime(dog.getWalkEndTime());
            }

            dogDto.setDogIntro(dog.getDogIntro());
            dogDto.setPersonalityType(dog.getPersonalityType());
            dogDto.setProfileUrl(dog.getProfileUrl());


            // ✅ 각 강아지별 성격 리스트 조회
            List<DogPersonal> dogPersonalList = dogPersonalRepository.findByDogId(dog.getDogId());
            if (!dogPersonalList.isEmpty()) {
                String selectedPersonalities = dogPersonalList.stream()
                        .map(DogPersonal::getDogPersonalGbnCd) // 코드 값 추출
                        .collect(Collectors.joining(",")); // 문자열로 변환
                dogDto.setSelectedPersonalities(selectedPersonalities);
            }

            // ✅ 각 강아지별 선호하는 놀이 리스트 조회
            List<DogPlay> dogPlayList = dogPlayRepository.findByDogId(dog.getDogId());
            if (!dogPlayList.isEmpty()) {
                String selectedPlays = dogPlayList.stream()
                        .map(DogPlay::getDogPlayGbnCd) // 코드 값 추출
                        .collect(Collectors.joining(",")); // 문자열로 변환
                dogDto.setSelectedPlays(selectedPlays);
            }

            //강아지 교배 파일 조회
            File file = fileService.findFileByFileRefNoAndFileGubnCode(dog.getDogId().toString(), "PE");
            if (file != null) {
                dogDto.setPeFile(file);
            }

            File file2 = fileService.findFileByFileRefNoAndFileGubnCode(dog.getDogId().toString(), "VA");
            if (file2 != null) {
                dogDto.setVaFile(file2);
            }

            File file3 = fileService.findFileByFileRefNoAndFileGubnCode(dog.getDogId().toString(), "HE");
            if (file3 != null) {
                dogDto.setHeFile(file3);
            }
            System.out.println(dogDto);

            return dogDto;
        }
        return null;
    }

    /**
     * 특정 강아지 삭제 (한 마리 남으면 삭제 불가)
     */
    @Transactional
    public boolean deleteDog(Integer dogId, String username) {
        List<Dog> userDogs = dogRepository.findByUsername(username);

        // 사용자의 강아지가 한 마리만 남아 있다면 삭제 불가
        if (userDogs.size() <= 1) {
            return false;
        }
        // 강아지 삭제
        dogRepository.deleteById(dogId);
        return true;
    }

    public List<Dog> getNearbyDogs(String username, double distance) {
        // 1. 사용자 위치 가져오기
        double[] userLocation = userService.getUserCoordinates(username);
        if (userLocation.length == 0) {
            throw new IllegalArgumentException("사용자 위치를 찾을 수 없습니다.");
        }
        else{
            // 2. 위도/경도 추출
            double latitude = userLocation[0];
            double longitude = userLocation[1];

            // 3. 사용자가 등록한 강아지 ID 목록 조회
            List<Dog> userDogIds = dogRepository.findUserDogIdsByUsername(username);
            System.out.println(userDogIds);
            List<Integer> dogIdList = new ArrayList<>();
            if(userDogIds != null && !userDogIds.isEmpty()) {
                for(Dog d : userDogIds) {
                    dogIdList.add(d.getDogId());
                }
            }else{
                dogIdList = List.of(-1);
            }

            // 4. 반경 `distance km` 내 강아지 검색 (본인 강아지 제외)
            return dogRepository.findDogsNearby(latitude, longitude, distance, dogIdList);
        }
    }
}
