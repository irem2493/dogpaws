package com.dogpaws.backend.service.hyepin;


import com.dogpaws.backend.dto.hyepin.TestDogDto;
import com.dogpaws.backend.repository.dao.hyepin.HyepinDogDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HyepinDogService {

    private final HyepinDogDao dogDao;

    public void insertDogs(List<TestDogDto> dogList) {
        int id = 1; // dogId를 1부터 순차적으로 지정
        for (TestDogDto dog : dogList) {
            // dogId를 수동으로 할당
            dog.setDogId(id++);
            // 기본정보 INSERT (여기서는 auto-generated key 대신 할당된 dogId 사용)
            dogDao.insertDog(dog);
            int dogId = dog.getDogId();
            log.info("Inserted dog with dogId={}", dogId);

            // 성격 정보 INSERT
            if (dog.getPersonalityTypes() != null) {
                for (String personality : dog.getPersonalityTypes()) {
                    dogDao.insertDogPersonal(dogId, personality);
                }
            }
            // 놀이 정보 INSERT
            if (dog.getPlayList() != null) {
                for (String play : dog.getPlayList()) {
                    dogDao.insertDogPlay(dogId, play);
                }
            }
        }
    }

}