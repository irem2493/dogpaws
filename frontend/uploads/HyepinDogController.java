package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.utils.DogDataGenerator;
import com.dogpaws.backend.service.hyepin.HyepinDogService;
import com.dogpaws.backend.dto.hyepin.TestDogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/dogs")
@RequiredArgsConstructor
@Slf4j
public class HyepinDogController {

    private final HyepinDogService dogService;

    @PostMapping("/insertOneDog100")
    public String insertOneDogWithId100() {
        TestDogDto oneDog = DogDataGenerator.generateDogList().get(0);
        oneDog.setDogId(100); // dogId를 100으로 지정
        dogService.insertDogs(Collections.singletonList(oneDog));
        return "Inserted one dog with dogId=100: " + oneDog.getDogName();
    }

    @GetMapping("/list")
    public List<TestDogDto> getDogList() {
        List<TestDogDto> dogList = DogDataGenerator.generateDogList();
        dogList.forEach(dog -> log.info("{}", dog));
        return dogList;
    }

    // 예: POST /dogs/insertAll 호출 시 DB에 전체 데이터 삽입
    @PostMapping("/insertAll")
    public String insertAllDogs() {
        // 전체 리스트에서 첫 번째 건만 선택
        TestDogDto oneDog = DogDataGenerator.generateDogList().get(0);
        dogService.insertDogs(Collections.singletonList(oneDog));
        return "Inserted one dog: " + oneDog.getDogName();
        /*
        List<TestDogDto> dogList = DogDataGenerator.generateDogList();
        dogService.insertDogs(dogList);
        return "Inserted " + dogList.size() + " dogs.";

         */
    }
}