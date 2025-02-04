package com.dogpaws.backend.service.common;

import com.dogpaws.backend.repository.dao.common.GubnDao;
import com.dogpaws.backend.dto.common.GubnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GubnService {

    private final GubnDao gubnDao;

    public List<GubnDto> getGubnList(String groupCode) {
        return gubnDao.getGubnList(groupCode);
    }
}
