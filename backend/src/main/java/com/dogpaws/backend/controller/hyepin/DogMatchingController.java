package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.ajy.DogResponseDto;
import com.dogpaws.backend.dto.hyepin.DogCandidateDto;
import com.dogpaws.backend.dto.hyepin.FilterDto;
import com.dogpaws.backend.dto.hyepin.MatchDto;
import com.dogpaws.backend.dto.hyepin.MatchingCriteriaDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.ajy.DogService;
import com.dogpaws.backend.service.common.LikeService;
import com.dogpaws.backend.service.hyepin.MatchingService;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@Slf4j
public class DogMatchingController {

    private final MatchingService matchingService;
    private final LikeService likeService;

    //매칭필터 가져오기
    @GetMapping("/filter")
    public FilterDto getFilterBydogId(@RequestParam("dogId") int dogId,
                                      @RequestParam("matchType") char matchType) throws IOException {
        log.info("여기는 백 컨트롤러 getFilterBydogId / dogId 값: {}", dogId);
        log.info("여기는 백 컨트롤러 getFilterBydogId / matchType 값: {}", matchType);
        FilterDto filterDto = matchingService.getFilterBydogId(dogId, matchType);
        log.info("여기는 백 컨트롤러 getFilterBydogId / FilterDto 값: {}", filterDto);
        return filterDto;
    }

    //매칭필터 등록 / 수정
    //match_type == "F / P" 확인하고, 정보 조회해서 null 값이면 등록, 아니면 수정 서비스로 보내기
    @PostMapping("/filter")
    public ApiResponse<String> setMatchingFilter(@ModelAttribute FilterDto filterDto) throws IOException {
        log.info("여기는 백 컨트롤러 setMatchingFilter / FilterDto 값: {}", filterDto);
        int result = matchingService.setMatchingFilter(filterDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 실패");
        }

    }

    //매칭필터 삭제
    //match_type == "F / P" 확인하고, 정보 조회해서 null 값이면 저장된 필터 데이터가 없습니다. 아니면 삭제완료
    @PostMapping("/filter/delete")
    public ApiResponse<String> deleteFilter(@RequestParam int dogId,
                                            @RequestParam char matchType) throws IOException {
        log.info("여기는 백 컨트롤러 deleteFilter / deleteFilter 값: {}", dogId);
        log.info("여기는 백 컨트롤러 deleteFilter / deleteFilter 값: {}", matchType);
        if (matchingService.getFilterBydogId(dogId, matchType) != null) {
            int result = matchingService.deleteFilter(dogId, matchType);
            log.info("여기는 백 컨트롤러 deleteFilter / result 값: {}", result);
            if (result == 1) {
                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 삭제 성공");
            }
        }
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 삭제 실패");
    }

    //친구매칭 가져오기
    @GetMapping
    public List<DogCandidateDto> getDogFriendMatchList(@RequestParam("dogId") int dogId,
                                                @RequestParam("username") String username,
                                                @RequestParam("matchType") String matchType) throws IOException {
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / dogId 값: {}", dogId);
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / username: {}", username);
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / matchType: {}", matchType);
        List<DogCandidateDto> matchList = matchingService.getFinalMatchingCandidates(dogId, username, matchType);
        //getDogFriendMatchList 좋아요 리스트 받아오기
        if(matchList != null && matchList.size() > 0) {
            matchList = likeService.getMatcingLike(username, matchList, matchType.charAt(0));
        }
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / matchList 값: {}", matchList);
        return matchList;
    }

    @PostMapping("/chat-room")
    public ApiResponse<String> inviteChatRoom(@ModelAttribute AlarmDto alarmDto) throws IOException {
        log.info("여기는 백 컨트롤러 inviteChatRoom / alarmDto 값: {}", alarmDto);
        int result = matchingService.inviteChatRoom(alarmDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "그룹 초대 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "그룹 초대 실패");
        }
    }

    //강아지 정보로 교배매칭 활성/비활성 체크 .. api 값 넘겨주면 프론트에서 Boolean값 체크
    @GetMapping("/detail/{dogId}")
    public Boolean getDog(@PathVariable Integer dogId) {
        DogResponseDto dog = matchingService.getIsMatingAvailable(dogId);
        System.out.println("getIsMatingAvailable:" + dog.getIsMatingAvailable());
        if(dog.getIsMatingAvailable().equals("N")){
            return false;
        }else{
            return true;
        }
    }
}
