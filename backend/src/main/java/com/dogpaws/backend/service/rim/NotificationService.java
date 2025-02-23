package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.NotificationScheduleDto;
import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.repository.dao.rim.FCMTokenDao;
import com.dogpaws.backend.repository.dao.rim.NotificationScheduleDao;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final FCMService fcmService;

    private final FCMTokenDao fcmTokenDao;

    private final AlarmDao alarmDao;

    private final UserRepository userRepository;

    private final NotificationScheduleDao scheduleDao;

    private final TaskScheduler taskScheduler;

    // 즉시 알림 발송
    public void sendNotification(String username, String message, String alarmType, String gubnId) {
        // FCM 발송
        String fcmToken = fcmTokenDao.getFcmToken(username);
        if (fcmToken != null) {
            // 알림 타입에 따라 제목 설정
            String title = alarmType.equals("C") ? "내일 일정이 있습니다" : 
                          message.contains("배송이 시작") ? "배송이 시작되었습니다" :
                          message.contains("배송이 완료") ? "배송이 완료되었습니다" : "주문 알림";
            
            fcmService.sendMessage(fcmToken, title, message);
        }
        // DB 저장
        saveAlarm(username, message, alarmType, gubnId);
    }

    // 예약 알림 등록
    public void scheduleNotification(
            String username,
            String message,
            String alarmType,
            String startDate,
            Long calendarId
    ) {
        NotificationScheduleDto schedule = NotificationScheduleDto.builder()
                .username(username)
                .calendarId(calendarId)
                .scheduleTime(LocalDateTime.parse(startDate).minusDays(1))
                .alarmType(alarmType)
                .gubnId(calendarId.toString())
                .message(message)
                .status("PENDING")
                .build();

        scheduleDao.insertSchedule(schedule);
        log.info("알림 예약 완료: username={}, calendarId={}", username, calendarId);
    }

    // 예약 알림 삭제
    public void deleteNotification(Long calendarId) {
        scheduleDao.deleteByCalendarId(calendarId);
        log.info("알림 예약 삭제 완료: calendarId={}", calendarId);
    }

    // FCM 토큰 저장
    public void saveFcmToken(String username, String token) {
        try {
            fcmTokenDao.saveFcmToken(username, token);
            log.info("FCM 토큰 저장 성공: {}", username);
        } catch (Exception e) {
            log.error("FCM 토큰 저장 실패: " + e.getMessage());
        }
    }

    // FCM 토큰 삭제 (로그아웃 시 호출)
    public void deleteFcmToken(String username) {
        try {
            fcmTokenDao.deleteFcmToken(username);
            log.info("FCM 토큰 삭제 성공: {}", username);
        } catch (Exception e) {
            log.error("FCM 토큰 삭제 실패: " + e.getMessage());
        }
    }

    private void saveAlarm(String username, String message, String alarmType, String gubnId) {
        AlarmDto alarm = new AlarmDto();
        alarm.setUsername(username);
        alarm.setAlarmType(alarmType);
        alarm.setGubnId(gubnId);
        alarm.setMessage(message);
        alarmDao.insertAlarm(alarm);
    }

    /**
     * 캘린더 알림 예약 처리
     */
    public void scheduleCalendarNotification(
            String username,
            String message,
            String alarmType,
            LocalDateTime startDate,
            Long calendarId
    ) {
        // 하루 전 시간 계산
        LocalDateTime notificationTime = startDate.minusDays(1);
        
        NotificationScheduleDto schedule = NotificationScheduleDto.builder()
                .username(username)
                .calendarId(calendarId)
                .scheduleTime(notificationTime)
                .alarmType(alarmType)
                .gubnId(calendarId.toString())
                .message(message)
                .status("PENDING")
                .build();

        scheduleDao.insertSchedule(schedule);
        log.info("알림 예약 완료: username={}, calendarId={}, 예약시간={}", 
                username, calendarId, notificationTime);
                
        // 3초 후에 예약된 알림 체크 실행
        Instant executionTime = Instant.now().plusSeconds(3);
        taskScheduler.schedule(() -> checkScheduledNotifications(), executionTime);
    }

    // 예약된 알림 체크 및 발송
    public void checkScheduledNotifications() {
        List<NotificationScheduleDto> schedules = scheduleDao
                .findPendingSchedules(LocalDateTime.now());

        for (NotificationScheduleDto schedule : schedules) {
            try {
                // 알림 발송
                sendNotification(
                    schedule.getUsername(),
                    schedule.getMessage(),
                    schedule.getAlarmType(),
                    schedule.getGubnId()
                );
                
                // 상태 업데이트
                scheduleDao.updateStatus(schedule.getId(), "SENT");
                log.info("예약 알림 발송 완료: scheduleId={}", schedule.getId());
            } catch (Exception e) {
                log.error("예약 알림 발송 실패: {}", e.getMessage(), e);
            }
        }
    }

}
