package com.dogpaws.backend.repository.dao.common;

import com.dogpaws.backend.dto.common.GubnDto;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmDao {
    public Integer insertAlarm(AlarmDto alarmDto);
}
