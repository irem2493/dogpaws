package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.hyepin.DogCandidateDto;
import com.dogpaws.backend.dto.hyepin.FilterDto;
import com.dogpaws.backend.dto.hyepin.MatchingCriteriaDto;
import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.repository.dao.hyepin.DogMatchDao;
import com.dogpaws.backend.utils.DefaultUtil;
import com.dogpaws.backend.utils.StringUtil;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final DogMatchDao dogMatchDao;
    private final AlarmDao alarmDao;

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

    //매칭필터 등록, 수정
    public int setMatchingFilter(FilterDto filterDto) {
        System.out.println("@@@@@@@@써비스 @@@@@@@ filterDto" + filterDto);
        // walkDayList 리스트들을 하나의 문자열로 만들기
        filterDto.setWalkDays(StringUtil.joinListToString(filterDto.getWalkDayList()));
        //update시 필요함. 리스트 잘라서 각 변수에 할당
        filterDto = StringUtil.listToString(filterDto);
        //널값 체크해서 default값 설정 해주기
        filterDto = DefaultUtil.SetDefault(filterDto);
        System.out.println("@@@@@@@@널값 체크 후 써비스 @@@@@@@ filterDto" + filterDto);

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

    //친구매칭 리스트 (1단계)
    public List<DogCandidateDto> getCandidateDogs(int dogId, String username, String matchType) {
        /*
        for(MatchDto matchDto : matchList){
            matchDto.setMatchedCriteriaList(StringUtil.splitToList(matchDto.getMatchedCriteria()));
            if(matchDto.getProfileUrl() != null){
                matchDto.setProfileUrl(matchDto.getProfileUrl().substring(matchDto.getProfileUrl().indexOf("/uploads")));
            }
        }
         */
        return dogMatchDao.getDogFriendMatchList(dogId, username, matchType);
    }

    // 매칭 점수 계산 (2단계)
    public int calculateMatchScore(DogCandidateDto candidate, MatchingCriteriaDto criteria) {
        int score = 0;

        // 1. 체중 조건
        if ("U".equals(criteria.getWeightCategory()) && candidate.getWeight() >= criteria.getWeight() - 1) {
            score += 7;
        } else if ("D".equals(criteria.getWeightCategory()) && candidate.getWeight() <= criteria.getWeight() + 1) {
            score += 7;
        }

        // 2. 성격 유형 조건
        if (candidate.getPersonalityType() != null && candidate.getPersonalityType().equals(criteria.getDogTypeCodeGbnCd())) {
            score += 7;
        }

        // 3. tbl_dog_personal 조건: 해당 항목 수에 2점씩 부여
        int personalCount = dogMatchDao.countPersonalMatches(candidate.getDogId(), criteria.getDogPersonalGbnCds());
        score += 2 * personalCount;

        // 4. tbl_dog_play 조건: 해당 항목 수에 2점씩 부여
        int playCount = dogMatchDao.countPlayMatches(candidate.getDogId(), criteria.getDogPlayGbnCds());
        score += 2 * playCount;

        // 5. 산책 시간 완벽 일치 (시작과 종료 시간이 모두 동일하면)
        if (candidate.getWalkStartTime() != null && candidate.getWalkEndTime() != null &&
                candidate.getWalkStartTime().equals(criteria.getWalkStartTime()) &&
                candidate.getWalkEndTime().equals(criteria.getWalkEndTime())) {
            score += 10;
        }

        // 6. 산책 요일: 쉼표로 구분된 문자열의 개수를 점수로 추가
        if (candidate.getWalkDays() != null && !candidate.getWalkDays().trim().isEmpty()) {
            int daysCount = candidate.getWalkDays().split(",").length;
            score += daysCount;
        }

        // 7. 산책 시작 시간 차이 (분 단위)
        if (candidate.getWalkStartTime() != null && criteria.getWalkStartTime() != null) {
            long diffStart = Math.abs(Duration.between(candidate.getWalkStartTime(), criteria.getWalkStartTime()).toMinutes());
            if (diffStart <= 30) {
                score += 10;
            } else if (diffStart <= 60) {
                score += 5;
            }
        }

        // 8. 산책 종료 시간 차이 (분 단위)
        if (candidate.getWalkEndTime() != null && criteria.getWalkEndTime() != null) {
            long diffEnd = Math.abs(Duration.between(candidate.getWalkEndTime(), criteria.getWalkEndTime()).toMinutes());
            if (diffEnd <= 30) {
                score += 10;
            } else if (diffEnd <= 60) {
                score += 5;
            }
        }
        return score;
    }

    // 매칭 기준 문자열 계산 (어떤 조건이 일치했는지 나열)
    public List<String> calculateMatchedCriteria(DogCandidateDto candidate, MatchingCriteriaDto criteria) {
        List<String> matchedCriteriaList = new ArrayList<>();

        if (candidate.getBreed().equals(criteria.getBreedGbnCd())) {
            matchedCriteriaList.add("품종");
        }

        if (candidate.getIsMix().equals(criteria.getIsMix())) {
            matchedCriteriaList.add("믹스여부");
        }

        if (("U".equals(criteria.getWeightCategory()) && candidate.getWeight() >= criteria.getWeight() - 1) ||
                ("D".equals(criteria.getWeightCategory()) && candidate.getWeight() <= criteria.getWeight() + 1)) {
            matchedCriteriaList.add("체중");
        }

        if (candidate.getPersonalityType() != null && candidate.getPersonalityType().equals(criteria.getDogTypeCodeGbnCd())) {
            matchedCriteriaList.add("성격유형");
        }

        int personalCount = dogMatchDao.countPersonalMatches(candidate.getDogId(), criteria.getDogPersonalGbnCds());
        if (personalCount > 0) {
            matchedCriteriaList.add("성격");
        }

        int playCount = dogMatchDao.countPlayMatches(candidate.getDogId(), criteria.getDogPlayGbnCds());
        if (playCount > 0) {
            matchedCriteriaList.add("놀이");
        }

        if (candidate.getWalkStartTime() != null && criteria.getWalkStartTime() != null &&
                candidate.getWalkEndTime() != null && criteria.getWalkEndTime() != null) {
            long diffStart = Math.abs(Duration.between(candidate.getWalkStartTime(), criteria.getWalkStartTime()).toMinutes());
            long diffEnd = Math.abs(Duration.between(candidate.getWalkEndTime(), criteria.getWalkEndTime()).toMinutes());
            if (diffStart <= 60 || diffEnd <= 60) {
                matchedCriteriaList.add("산책시간");
            }
        }

        if (candidate.getWalkDays() != null && !candidate.getWalkDays().trim().isEmpty()) {
            int daysCount = candidate.getWalkDays().split(",").length;
            if (daysCount > 0) {
                matchedCriteriaList.add("산책요일");
            }
        }

        return matchedCriteriaList;
    }

    // 최종 후보군 반환: 지역 일치 우선 정렬 + 매칭 점수 내림차순 + 난수 섞기
    public List<DogCandidateDto> getFinalMatchingCandidates(int dogId, String username, String matchType) {

        //매칭필터 가져오기
        MatchingCriteriaDto criteria = dogMatchDao.getMatchingCriteria(dogId, username);

        // 1단계: DB에서 후보 목록 가져오기
        List<DogCandidateDto> candidates = getCandidateDogs(dogId, username, matchType);

        // 2단계: 각 후보에 대해 매칭 점수와 매칭 기준 계산
        for (DogCandidateDto candidate : candidates) {
            int score = calculateMatchScore(candidate, criteria);
            candidate.setMatchScore(score);
            List<String> criteriaList = calculateMatchedCriteria(candidate, criteria);
            candidate.setMatchedCriteriaList(criteriaList);
        }

        // 지역 일치 여부: candidate.getDogRegion()와 candidate.getUserRegion() 비교 (같으면 우선순위 높게)
        // 최종 정렬: (1) 지역 일치, (2) 매칭 점수 내림차순, (3) 난수(랜덤 섞기)
        List<DogCandidateDto> sortedCandidates = candidates.stream()
                .sorted((a, b) -> {
                    // 지역 일치 여부: 같은 지역이면 1, 아니면 0
                    int aRegionMatch = a.getDogRegion().equals(a.getUserRegion()) ? 1 : 0;
                    int bRegionMatch = b.getDogRegion().equals(b.getUserRegion()) ? 1 : 0;
                    if (aRegionMatch != bRegionMatch) {
                        return Integer.compare(bRegionMatch, aRegionMatch);
                    }
                    // 매칭 점수 내림차순
                    int scoreDiff = b.getMatchScore() - a.getMatchScore();
                    if (scoreDiff != 0) {
                        return scoreDiff;
                    }
                    // 마지막으로 랜덤 정렬 (난수 비교)
                    return Double.compare(Math.random(), Math.random());
                })
                .limit(20)  // 상위 20건만 선택
                .collect(Collectors.toList());
        return sortedCandidates;
    }




    //그룹채팅 초대
    public int inviteChatRoom(AlarmDto alarmDto){
        int result = alarmDao.insertAlarm(alarmDto);
        return result;
    }



}
