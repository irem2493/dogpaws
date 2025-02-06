package com.dogpaws.backend.service.common;

import com.dogpaws.backend.repository.dao.common.LikeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
}
