package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.dto.rim.NotificationScheduleDto;
import com.dogpaws.backend.repository.dao.rim.NotificationScheduleDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class NotificationScheduleService {

    private final NotificationScheduleDao notificationScheduleDao;

    @Transactional
    public void scheduleCalendarNotification(CalendarDto calendar,String alarmType) {
        NotificationScheduleDto schedule = NotificationScheduleDto.builder()
                .username(calendar.getUsername())
                .calendarId(calendar.getCalendarId().longValue())  // 추가
                .scheduleTime(LocalDateTime.parse(calendar.getCalendarStartDate()).minusDays(1))
                .alarmType(alarmType)
                .gubnId(calendar.getCalendarId().toString())  // 추가
                .message(String.format("내일 일정이 있습니다: %s", calendar.getCalendarTitle()))
                .status("PENDING")
                .build();

        notificationScheduleDao.insertSchedule(schedule);
    }

    @Transactional
    public void updateCalendarNotification(CalendarDto calendar) {
        notificationScheduleDao.deleteByCalendarId(calendar.getCalendarId().longValue());
        scheduleCalendarNotification(calendar,"C");
    }

    @Transactional
    public void deleteCalendarNotification(Long calendarId) {
        notificationScheduleDao.deleteByCalendarId(calendarId);
    }

    @Transactional
    public void deleteSharedCalendarNotification(Long calendarId, String username) {
        notificationScheduleDao.deleteByCalendarIdAndUsername(calendarId, username);
    }
}