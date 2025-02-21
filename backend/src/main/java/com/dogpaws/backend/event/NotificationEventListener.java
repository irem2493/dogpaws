package com.dogpaws.backend.event;

import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.service.rim.NotificationService;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final AlarmDao alarmDao;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("알림 이벤트 수신: type={}, username={}",
                event.getUsername());
        try {
            if (event.getUsername() == null) {
                // 전체 발송 로직
                notificationService.sendNotificationToAll(
                        event.getMessage(),
                        event.getAlarmType()
                );
            } else {
                // 특정 사용자 발송
                notificationService.sendNotification(
                        event.getUsername(),
                        event.getMessage(),
                        event.getAlarmType()
                );
            }

            AlarmDto alarm = new AlarmDto();
            alarm.setUsername(event.getUsername());
            alarm.setAlarmType("C");  // Calendar
            alarm.setGubnId(event.getGubnId());
            alarm.setMessage(event.getMessage());

            alarmDao.insertAlarm(alarm);

        } catch (Exception e) {
            log.error("알림 발송 실패: {}", e.getMessage());
        }
    }
}