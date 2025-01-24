package com.dogpaws.backend.service.common;

import org.green.backend.dto.common.GubnDto;
import org.green.backend.repository.dao.common.GubnDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 12-28 (작성자: 안제연)
 * 이 클래스는 채용공고의 셀렉트박스에 값을 넣는데 사용되는 구분 Service입니다.
 */
@Service
public class GubnService {

    @Autowired
    private GubnDao gubnDao;

    public List<GubnDto> getGubnList(String gubnCode) {
        return gubnDao.getGubnList(gubnCode);
    };

    public List<GubnDto> getSkillList() {
        return gubnDao.getSkillList();
    }

    public List<GubnDto> getSkillName(String stackCode){return gubnDao.getSkillName(stackCode);};

}
