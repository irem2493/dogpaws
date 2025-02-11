package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.hyepin.FilterDto;
import com.dogpaws.backend.dto.hyepin.MatchDto;
import com.dogpaws.backend.repository.dao.hyepin.DogMatchDao;
import com.dogpaws.backend.utils.StringUtil;
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
    public FilterDto getFilterBydogId(int dogId, char matchType) {
        FilterDto filterDto = dogMatchDao.getFilterBydogIdAndMatchType(dogId, matchType);
        if(filterDto != null) {
            filterDto.setDogPlayList(StringUtil.splitToList(filterDto.getDogPlay()));
            filterDto.setDogPlayGbnCdList(StringUtil.splitToList(filterDto.getDogPlayGbnCd()));
            filterDto.setDogPersonalList(StringUtil.splitToList(filterDto.getDogPersonal()));
            filterDto.setDogPersonalGbnCdList(StringUtil.splitToList(filterDto.getDogPersonalGbnCd()));
            filterDto.setWalkDayList(StringUtil.splitToList(filterDto.getWalkDays()));
            filterDto.setStrWalkStartTime(StringUtil.formatTime(filterDto.getWalkStartTime()));
            filterDto.setStrWalkEndTime(StringUtil.formatTime(filterDto.getWalkEndTime()));
        }
        return filterDto;
    }

    //매칭 등록, 수정
    public int setMatchingFilter(FilterDto filterDto) {
        System.out.println("@@@@@@@@써비스 @@@@@@@ filterDto" + filterDto);
        int result = 0;
        if(getFilterBydogId(filterDto.getDogId(), filterDto.getMatchType()) == null){
            result = dogMatchDao.insertFilter(filterDto);
        }else if(getFilterBydogId(filterDto.getDogId(), filterDto.getMatchType()) != null){
            result = dogMatchDao.updateFilter(filterDto);
        }
        return result;
    }

    //매칭필터 삭제하기
    public int deleteFilter(int dogId, char matchType) {
        int result = dogMatchDao.deleteFilter(dogId, matchType);
        return result;
    }

    //친구매칭 리스트
    public List<MatchDto> getDogFriendMatchList(int dogId, String username) {
        List<MatchDto> matchList = dogMatchDao.getDogFriendMatchList(dogId, username);
        for(MatchDto matchDto : matchList){
            matchDto.setMatchedCriteriaList(StringUtil.splitToList(matchDto.getMatchedCriteria()));
            if(matchDto.getProfileUrl() != null){
                matchDto.setProfileUrl(matchDto.getProfileUrl().substring(matchDto.getProfileUrl().indexOf("/uploads")));
            }
        }
        return matchList;
    }



}
