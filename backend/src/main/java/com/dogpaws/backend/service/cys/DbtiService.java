package com.dogpaws.backend.service.cys;

import com.dogpaws.backend.repository.dao.cys.DbtiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2025-02-04 by 최윤서
 */
@Service
public class DbtiService {

    @Autowired
    private DbtiDao dao;

    public void dogType(String type, int dogId) {dao.dogType(type, dogId);}

}
