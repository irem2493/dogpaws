package com.dogpaws.backend.utils.scheduler;

import com.dogpaws.backend.dto.rim.NotificationScheduleDto;
import com.dogpaws.backend.event.NotificationEvent;
import com.dogpaws.backend.repository.dao.rim.NotificationScheduleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class checkScheduledNotifications {

    private final NotificationScheduleDao notificationScheduleDao;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0/20 * * * * *")
    public void checkScheduledNotifications() {
        log.info("예약된 알림 체크 시작: {}", LocalDateTime.now());

        try {
            // 현재 시간에 발송해야 할 알림들 조회
            List<NotificationScheduleDto> schedules = notificationScheduleDao
                    .findPendingSchedules(LocalDateTime.now());

            for (NotificationScheduleDto schedule : schedules) {
                // 알림 이벤트 발행
                eventPublisher.publishEvent(
                        NotificationEvent.calendarReminder(
                                schedule.getUsername(),
                                schedule.getCalendarTitle()
                        )
                );
                log.info("알림 이벤트 발행 완료");
                
                // 상태 업데이트
                notificationScheduleDao.updateStatus(schedule.getId(), "SENT");
            }

            log.info("알림 발송 완료: {} 건", schedules.size());
        } catch (Exception e) {
            log.error("알림 발송 실패: ", e);
        }
    }
}
