package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.DogMatchDto;
import com.dogpaws.backend.global.common.ApiResponse;
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

    //매칭필터 가져오기
    @GetMapping
    public DogMatchDto getFilterBydogId(@RequestParam("dogId") int dogId,
                                        @RequestParam("matchType") char matchType) throws IOException {
        log.info("여기는 백 컨트롤러 getFilterBydogId / dogId 값: {}", dogId);
        log.info("여기는 백 컨트롤러 getFilterBydogId / matchType 값: {}", matchType);
        DogMatchDto dogMatchDto = matchingService.getFilterBydogId(dogId, matchType);
        log.info("여기는 백 컨트롤러 getFilterBydogId / dogMatchDto 값: {}", dogMatchDto);
        return dogMatchDto;
    }

    //매칭필터 등록 / 수정
    //match_type == "F / P" 확인하고, 정보 조회해서 null 값이면 등록, 아니면 수정 서비스로 보내기
    @PostMapping
    public ApiResponse<String> setMatchingFilter(@ModelAttribute DogMatchDto dogMatchDto) throws IOException {
        log.info("여기는 백 컨트롤러 setMatchingFilter / dogMatchDto 값: {}", dogMatchDto);
        int result = matchingService.setMatchingFilter(dogMatchDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 등록 실패");
        }
    }

    //매칭필터 삭제
    //match_type == "F / P" 확인하고, 정보 조회해서 null 값이면 저장된 필터 데이터가 없습니다. 아니면 삭제완료
    @PostMapping("delete")
    public ApiResponse<String> deleteFilter(@RequestParam int dogId,
                                                 @RequestParam char matchType) throws IOException {
        log.info("여기는 백 컨트롤러 deleteFilter / deleteFilter 값: {}", dogId, matchType);
        int result = matchingService.deleteFilter(dogId, matchType);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 삭제 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "필터 삭제 실패");
        }
    }



}