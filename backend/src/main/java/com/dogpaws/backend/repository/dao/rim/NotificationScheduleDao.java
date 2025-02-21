package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.NotificationScheduleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationScheduleDao {
    void insertSchedule(NotificationScheduleDto schedule);

    void deleteByCalendarId(@Param("calendarId") Long calendarId);

    void deleteByCalendarIdAndUsername(
            @Param("calendarId") Long calendarId,
            @Param("username") String username
    );

    List<NotificationScheduleDto> findPendingSchedules(
            @Param("currentTime") LocalDateTime currentTime
    );

    void updateStatus(
            @Param("alarm_schedules_id") Long id,
            @Param("status") String status
    );
}