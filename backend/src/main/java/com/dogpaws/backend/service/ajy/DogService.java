package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.DogDto;
import com.dogpaws.backend.dto.ajy.DogResponseDto;
import com.dogpaws.backend.entity.ajy.Dog;
import com.dogpaws.backend.entity.ajy.DogPersonal;
import com.dogpaws.backend.entity.ajy.DogPlay;
import com.dogpaws.backend.repository.jpa.ajy.DogPersonalRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogPlayRepository;
import com.dogpaws.backend.repository.jpa.ajy.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final DogPersonalRepository dogPersonalRepository;
    private final DogPlayRepository dogPlayRepository;

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
        List<DogPersonal> dogPersonalList = null;
        List<DogPlay> dogPlayList = null;

        for (Dog d : dogList) {
            dogIdList.add(d.getDogId());
        }

        for (Integer dogId : dogIdList) {
            dogPersonalList = dogPersonalRepository.findByDogId(dogId);
            dogPlayList = dogPlayRepository.findByDogId(dogId);
        }

        for (Dog d : dogList) {
            DogResponseDto dogDto = new DogResponseDto();
            dogDto.setUsername(d.getUsername());
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
            if(d.getWalkTimeYn() != null && d.getWalkTimeYn().equals("Y")) {
                dogDto.setWalkStartTime(d.getWalkStartTime());
                dogDto.setWalkEndTime(d.getWalkEndTime());
            }

            dogDto.setDogIntro(d.getDogIntro());
            dogDto.setPersonalityType(d.getPersonalityType());
            dogDto.setProfileUrl(d.getProfileUrl());

            if(dogPersonalList != null){
                StringBuilder result = new StringBuilder();
                for(DogPersonal p : dogPersonalList){
                    result.append(p.getDogPersonalGbnCd()).append(",");
                }

                // 마지막 쉼표 제거
                if (!result.isEmpty()) {
                    result.setLength(result.length() - 1);
                }

                dogDto.setSelectedPersonalities(result.toString());
            }

            if(dogPlayList != null){
                StringBuilder result = new StringBuilder();
                for(DogPlay p : dogPlayList){
                    result.append(p.getDogPlayGbnCd()).append(",");
                }

                // 마지막 쉼표 제거
                if (!result.isEmpty()) {
                    result.setLength(result.length() - 1);
                }

                dogDto.setSelectedPersonalities(result.toString());
            }

            dogResponseDtoList.add(dogDto);
        }

        return dogResponseDtoList;
    }
}
