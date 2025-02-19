package com.dogpaws.backend.service.cys;

import com.dogpaws.backend.dto.cys.DogDto;
import com.dogpaws.backend.repository.dao.cys.MainDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2025-02-19 by 최윤서
 */
@Service
public class MainService {

    @Autowired
    private MainDao mainDao;

    public DogDto getDogById(int dogId) {
        return mainDao.getDogById(dogId);
    }
}
