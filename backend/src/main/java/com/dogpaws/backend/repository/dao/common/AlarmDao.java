package com.dogpaws.backend.repository.dao.common;

import com.dogpaws.backend.dto.common.GubnDto;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmDao {
    public Integer insertAlarm(AlarmDto alarmDto);

    // 알림 조회 메서드 추가
    List<AlarmDto> getAlarms(@Param("username") String username);

    // 읽지 않은 알림 개수 조회
    Integer getUnreadAlarmCount(@Param("username") String username);

    // 알림 읽음 처리
    void markAlarmAsRead(@Param("alarmId") Long alarmId);
}
