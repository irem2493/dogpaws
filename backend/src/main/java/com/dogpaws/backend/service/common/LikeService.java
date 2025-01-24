package com.dogpaws.backend.service.common;

import lombok.RequiredArgsConstructor;
import org.green.backend.repository.dao.common.LikeDao;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeDao likeDao;

    public void toggleLike(String username, String likeCode, String likeId) {
        Integer isCheck = likeDao.checkLike(username, likeCode, likeId);

        if (isCheck != null && isCheck > 0) {
            likeDao.deleteLike(username, likeCode, likeId);
        } else {
            likeDao.insertLike(username, likeCode, likeId);
        }
    }
}
