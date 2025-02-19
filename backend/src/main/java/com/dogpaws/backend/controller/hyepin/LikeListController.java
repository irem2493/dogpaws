package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.FilterDto;
import com.dogpaws.backend.dto.hyepin.LikeDogDto;
import com.dogpaws.backend.repository.dao.hyepin.LikeDogDao;
import com.dogpaws.backend.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
@Slf4j
public class LikeListController {

    private final LikeDogDao likeDogDao;

    //좋아요 리스트 가져오기
    @GetMapping
    public List<LikeDogDto> getLikeList(@RequestParam("myDogId") int myDogId,
                                 @RequestParam("likeCode") String likeCode) throws IOException {
        log.info("여기는 백 컨트롤러 getLikeList / dogId 값: {}", myDogId);
        log.info("여기는 백 컨트롤러 getLikeList / likeCode 값: {}", likeCode);
        List<LikeDogDto> likeDogDtoList = likeDogDao.getLikeList(myDogId, likeCode);
        for(LikeDogDto likeDog : likeDogDtoList){
            likeDog.setDogPersonalGbnCdsList(StringUtil.splitToList(likeDog.getDogPersonalGbnCds()));
            likeDog.setDogPlayGbnCdsList(StringUtil.splitToList(likeDog.getDogPlayGbnCds()));
        }
        log.info("여기는 백 컨트롤러 getLikeList / likeDogDto 값: {}", likeDogDtoList);
        return likeDogDtoList;
    }
}
