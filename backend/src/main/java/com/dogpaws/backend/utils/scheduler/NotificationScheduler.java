package com.dogpaws.backend.utils.scheduler;

import com.dogpaws.backend.dto.rim.NotificationScheduleDto;
import com.dogpaws.backend.repository.dao.rim.NotificationScheduleDao;
import com.dogpaws.backend.service.rim.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {
    private final NotificationScheduleDao scheduleDao;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 * * * *")
//    @Scheduled(cron = "0/30 * * * * *") //test용
    public void checkScheduledNotifications() {
        List<NotificationScheduleDto> schedules = scheduleDao
                .findPendingSchedules(LocalDateTime.now());

        for (NotificationScheduleDto schedule : schedules) {
            notificationService.sendNotification(
                    schedule.getUsername(),
                    schedule.getMessage(),
                    schedule.getAlarmType(),
                    schedule.getGubnId()
            );


            scheduleDao.updateStatus(schedule.getId(), "SENT");
        }
    }
}