package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.FilterDto;
import com.dogpaws.backend.dto.hyepin.MatchDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.common.LikeService;
import com.dogpaws.backend.service.hyepin.MatchingService;
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
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 성공");
        /*
        int result = matchingService.setMatchingFilter(filterDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 실패");
        }

         */
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
    public List<MatchDto> getDogFriendMatchList(@RequestParam("dogId") int dogId,
                                                @RequestParam("username") String username) throws IOException {
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / dogId 값: {}", dogId);
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / username: {}", username);
        List<MatchDto> matchList = matchingService.getDogFriendMatchList(dogId, username);
        //getDogFriendMatchList 좋아요 리스트 받아오기
        matchList = likeService.getMatcingLike(username, matchList, 'F');
        log.info("여기는 백 컨트롤러 getDogFriendMatchList / matchList 값: {}", matchList);
        return matchList;
    }


}