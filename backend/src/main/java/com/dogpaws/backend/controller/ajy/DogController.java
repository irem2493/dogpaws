package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.DogDto;
import com.dogpaws.backend.dto.ajy.DogRegisterRequestDto;
import com.dogpaws.backend.dto.ajy.DogResponseDto;
import com.dogpaws.backend.service.ajy.DogService;
import com.dogpaws.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dog")
public class DogController {

    private final DogService dogService;

    @GetMapping("/dogList/{username}")
    public List<DogDto> getDogList(@PathVariable String username) {
        return dogService.getDogs(username);
    }

    @GetMapping("/mypage/dogList/{username}")
    public  List<DogResponseDto> getDogList2(@PathVariable String username) {
        return dogService.getDogList(username);
    }

    @GetMapping("/detail/{dogId}")
    public DogResponseDto getDog(@PathVariable Integer dogId) {
        System.out.println(dogService.getDog(dogId));
        return dogService.getDog(dogId);
    }

    //강아지 정보 삭제
   @DeleteMapping("/{dogId}/{username}")
    public ApiResponse<?> deleteDog(@PathVariable("dogId") Integer dogId, @PathVariable("username") String username) {
        if(dogService.deleteDog(dogId, username)){
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "강아지 삭제 완료");
        }
        else{
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "강아지 삭제 실패");
        }
   }

    @PostMapping("/{username}")
    public ApiResponse<?> getStep1(@PathVariable String username, @ModelAttribute DogRegisterRequestDto dogRegisterRequestDto) {
        dogRegisterRequestDto.setUsername(username);

        System.out.println(dogRegisterRequestDto);

        //현재 파일 안 옴

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "강아지 등록 완료");
    }

}
