package com.dogpaws.backend.service.common;

import com.dogpaws.backend.dto.hyepin.DogCandidateDto;
import com.dogpaws.backend.dto.hyepin.MatchDto;
import com.dogpaws.backend.repository.dao.common.LikeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeDao likeDao;

    public int toggleLike(String username, char likeCode, int dogId) {
        Integer isCheck = likeDao.checkLike(username, likeCode, dogId);
        int result = 0;
        if (isCheck != null && isCheck > 0) {
            result = likeDao.deleteLike(username, likeCode, dogId);
            return result;
        } else {
            result = likeDao.insertLike(username, likeCode, dogId);
            return result;
        }
    }


    public List<DogCandidateDto> getMatcingLike (String username, List<DogCandidateDto> matchList, char likeCode) {
        for (DogCandidateDto m : matchList) {
            int check = likeDao.checkLike(username, likeCode, m.getDogId());
            if (check == 0) {
                m.setLiked(false);
            }else if(check == 1){
                m.setLiked(true);
            }
        }
        return matchList;
    }


}
