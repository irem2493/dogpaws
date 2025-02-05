package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.hyepin.DogMatchDto;
import com.dogpaws.backend.repository.dao.hyepin.DogMatchDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final DogMatchDao dogMatchDao;

    //강아지 Id, 매칭 타입에 따른 매칭필터 받아오기
    public DogMatchDto getFilterBydogId(int dogId, char matchType) {
        DogMatchDto dogMatchDto = dogMatchDao.getFilterBydogIdAndMatchType(dogId, matchType);
        return dogMatchDto;
    }

    //매칭 등록, 수정
    public int setMatchingFilter(DogMatchDto dogMatchDto) {
        int result = 0;
        if(getFilterBydogId(dogMatchDto.getDogId(), dogMatchDto.getMatchType()) == null){
            result = dogMatchDao.insertFilter(dogMatchDto);
        }else if(getFilterBydogId(dogMatchDto.getDogId(), dogMatchDto.getMatchType()) != null){
            result = dogMatchDao.updateFilter(dogMatchDto);
        }
        return result;
    }

    //매칭필터 삭제하기
    public int deleteFilter(int dogId, char matchType) {
        int result = dogMatchDao.deleteFilter(dogId, matchType);
        return result;
    }



}
