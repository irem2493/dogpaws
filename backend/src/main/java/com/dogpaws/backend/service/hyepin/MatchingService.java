package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.ajy.DogResponseDto;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final DogMatchDao dogMatchDao;
    private final AlarmDao alarmDao;

    //강아지 Id, 매칭 타입에 따른 매칭필터 받아오기
    public FilterDto getFilterBydogId(int dogId, char matchType) {
        FilterDto filterDto = dogMatchDao.getFilterBydogIdAndMatchType(dogId, matchType);
        if (filterDto != null) {
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
        if (getFilterBydogId(filterDto.getDogId(), filterDto.getMatchType()) == null) {
            result = dogMatchDao.insertFilter(filterDto);
        } else if (getFilterBydogId(filterDto.getDogId(), filterDto.getMatchType()) != null) {
            result = dogMatchDao.updateFilter(filterDto);
        }
        return result;
    }

    //매칭필터 삭제하기
    public int deleteFilter(int dogId, char matchType) {
        int result = dogMatchDao.deleteFilter(dogId, matchType);
        return result;
    }

    //친구매칭 리스트 (1단계) (필터없는 기본용)
    public List<DogCandidateDto> getDogDefaultMatchList(int dogId, String matchType) {

        List<DogCandidateDto> DogCandidateDtoList = dogMatchDao.getDogDefaultMatchList(dogId, matchType);
        log.info("친구매칭 리스트 (1단계) DogCandidateDtoList" + DogCandidateDtoList);
        return DogCandidateDtoList;
    }

    //친구매칭 리스트 (1단계) (필터 있음)
    public List<DogCandidateDto> getCandidateDogs(int dogId, String username, String matchType, String bloodTestCertified, String vaccinationCertified, String healthRecordCertified) {
        List<DogCandidateDto> DogCandidateDtoList = dogMatchDao.getDogMatchList(dogId, username, matchType, bloodTestCertified, vaccinationCertified, healthRecordCertified);
        log.info("친구매칭 리스트 (1단계) DogCandidateDtoList" + DogCandidateDtoList);
        return DogCandidateDtoList;
    }

    // 매칭 점수 / 기준 계산 (2단계)
    public DogCandidateDto calculateMatching(DogCandidateDto candidate, MatchingCriteriaDto criteria) {
        System.out.println("calculateMatching 서비스임@@@@@@@");
        System.out.println("candidate: " + candidate);
        System.out.println("criteria: " + criteria);

        int score = 0;
        boolean walkTimeMatched = false;
        List<String> matchedCriteriaList = new ArrayList<>();

        //견종 조건
        if (candidate.getBreed().equals(criteria.getBreedGbnCd())) {
            matchedCriteriaList.add("품종");
        }

        //서류 조건
        if (("Y".equals(criteria.getBloodTestCertified()) && "Y".equals(candidate.getBloodTestCertified())) ||
                    ("Y".equals(criteria.getVaccinationCertified()) && "Y".equals(candidate.getVaccinationCertified())) ||
                    ("Y".equals(criteria.getHealthRecordCertified()) && "Y".equals(candidate.getHealthRecordCertified()))) {
          matchedCriteriaList.add("서류");
        }


        //체중 조건
        if ("U".equals(criteria.getWeightCategory()) && candidate.getWeight() >= criteria.getWeight() - 1) {
            score += 7;
            matchedCriteriaList.add("체중");
        } else if ("D".equals(criteria.getWeightCategory()) && candidate.getWeight() <= criteria.getWeight() + 1) {
            score += 7;
            matchedCriteriaList.add("체중");
        }
        System.out.println("체중 후 score: " + score);

        //성격 유형 조건
        if (candidate.getPersonalityType() != null && candidate.getPersonalityType().equals(criteria.getDogTypeCodeGbnCd())) {
            score += 7;
            matchedCriteriaList.add("성격유형");
        }
        System.out.println("성격 유형 후 score: " + score);

        //tbl_dog_personal 조건: 해당 항목 수에 2점씩 부여
        if(!criteria.getDogPersonalGbnCdsList().isEmpty()){
            int personalCount = dogMatchDao.countPersonalMatches(candidate.getDogId(), criteria.getDogPersonalGbnCdsList());
            if (personalCount > 0) {
                matchedCriteriaList.add("성격");
            }
            score += 2 * personalCount;
            System.out.println("tbl_dog_personal score: " + score);
        }

        if(!criteria.getDogPlayGbnCdsList().isEmpty()){
            //tbl_dog_play 조건: 해당 항목 수에 2점씩 부여
            int playCount = dogMatchDao.countPlayMatches(candidate.getDogId(), criteria.getDogPlayGbnCdsList());
            if (playCount > 0) {
                matchedCriteriaList.add("놀이");
            }
            score += 2 * playCount;
            System.out.println("tbl_dog_play score: " + score);
        }

        //산책 시작 시간 차이 (분 단위)
        if (candidate.getWalkStartTime() != null && criteria.getWalkStartTime() != null) {
            long diffStart = Math.abs(Duration.between(candidate.getWalkStartTime(), criteria.getWalkStartTime()).toMinutes());
            if (diffStart <= 30) {
                score += 10;
                walkTimeMatched = true;
            } else if (diffStart <= 60) {
                score += 5;
                walkTimeMatched = true;
            }
        }
        System.out.println("산책 시작 시간 score : " + score);

        //산책 종료 시간 차이 (분 단위)
        if (candidate.getWalkEndTime() != null && criteria.getWalkEndTime() != null) {
            long diffEnd = Math.abs(Duration.between(candidate.getWalkEndTime(), criteria.getWalkEndTime()).toMinutes());
            if (diffEnd <= 30) {
                score += 10;
                walkTimeMatched = true;
            } else if (diffEnd <= 60) {
                score += 5;
                walkTimeMatched = true;
            }
        }
        System.out.println("산책 종료 시간 score : " + score);

        if (walkTimeMatched) {
            matchedCriteriaList.add("산책시간");
        }

        //산책 요일 조건
        List<String> candidateDays = StringUtil.splitToList(candidate.getWalkDays())
                .stream()
                .filter(day -> day != null && !day.trim().isEmpty()) // null 및 빈 문자열 체크 추가
                .map(day -> day.length() > 0 ? day.substring(0, 1) : "") // 길이 체크 후 substring 실행
                .collect(Collectors.toList());

        List<String> criteriaDays = StringUtil.splitToList(criteria.getWalkDays())
                .stream()
                .filter(day -> day != null && !day.trim().isEmpty()) // null 및 빈 문자열 체크 추가
                .map(day -> day.length() > 0 ? day.substring(0, 1) : "") // 길이 체크 후 substring 실행
                .collect(Collectors.toList());

        Set<String> commonDays = new HashSet<>(candidateDays);
        commonDays.retainAll(criteriaDays);

        if (!commonDays.isEmpty()) {
            score += commonDays.size(); // 공통 요일 수 만큼 점수 추가
            matchedCriteriaList.add("산책요일");
        } else {
            System.out.println("공통된 산책 요일이 없습니다.");
        }

        System.out.println("산책요일 score: " + score);

        candidate.setMatchScore(score);
        candidate.setMatchedCriteriaList(matchedCriteriaList);
        return candidate;
    }


    // 최종 후보군 반환: 지역 일치 우선 정렬 + 매칭 점수 내림차순 + 난수 섞기
    public List<DogCandidateDto> getFinalMatchingCandidates(int dogId, String username, String matchType) {

        //매칭필터 가져오기
        MatchingCriteriaDto criteria = dogMatchDao.getMatchingCriteria(dogId, username, matchType);
        System.out.println("여기 매칭 서비스!@@@@@@@@@@@@@@@@@@@@@ 매칭 타입 어떻게 되냐? " + criteria);
        //후보리스트 초기화
        List<DogCandidateDto> candidates;

        //필터 기준이 없을 때
        if(criteria == null){
            //매칭 필터 없을 때 강아지 정보랑 비교하기
            criteria = dogMatchDao.getDogCriteria(dogId);
            //문자열 잘라서 리스트 넣기
            criteria.setDogPersonalGbnCdsList(StringUtil.splitToList(criteria.getDogPersonalGbnCds()));
            criteria.setDogPlayGbnCdsList(StringUtil.splitToList(criteria.getDogPlayGbnCds()));
            // 1단계: DB에서 후보 목록 가져오기
            candidates = getDogDefaultMatchList(dogId, matchType);
        }else {
            //문자열 잘라서 리스트 넣기
            criteria.setDogPersonalGbnCdsList(StringUtil.splitToList(criteria.getDogPersonalGbnCds()));
            criteria.setDogPlayGbnCdsList(StringUtil.splitToList(criteria.getDogPlayGbnCds()));
            // 1단계: DB에서 후보 목록 가져오기
            candidates = getCandidateDogs(dogId, username, matchType, criteria.getBloodTestCertified(), criteria.getVaccinationCertified(), criteria.getHealthRecordCertified());
        }

        System.out.println("dto 확인@@@@@@@@@@ criteria: " + criteria);
        System.out.println("dto 확인@@@@@@@@@@ candidates: " + candidates);

        // 2단계: 각 후보에 대해 매칭 점수와 매칭 기준 계산
        for (DogCandidateDto candidate : candidates) {
            candidate = calculateMatching(candidate, criteria);
            System.out.println("candidate: " + candidate);
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
    public int inviteChatRoom(AlarmDto alarmDto) {
        int result = alarmDao.insertAlarm(alarmDto);
        return result;
    }

    //교배매칭 활성화 / 비활성화
    public DogResponseDto getIsMatingAvailable(Integer dogId) {
        DogResponseDto dog = dogMatchDao.getIsMatingAvailable(dogId);
        System.out.println("dog 정보: " + dog);
        return dog;
    }


}
