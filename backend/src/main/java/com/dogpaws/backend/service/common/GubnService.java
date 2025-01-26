package com.dogpaws.backend.service.common;

import com.dogpaws.backend.repository.dao.common.GubnDao;
import com.dogpaws.backend.dto.common.GubnDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GubnService {

    @Autowired
    private GubnDao gubnDao;


}
