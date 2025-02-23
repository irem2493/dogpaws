package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.rim.NotificationScheduleDto;
import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.repository.dao.rim.FCMTokenDao;
import com.dogpaws.backend.repository.dao.rim.NotificationScheduleDao;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final FCMService fcmService;

    private final FCMTokenDao fcmTokenDao;

    private final AlarmDao alarmDao;

    private final UserRepository userRepository;

    private final NotificationScheduleDao scheduleDao;


    // 즉시 알림 발송
    public void sendNotification(String username, String message, String alarmType, String gubnId) {
        // FCM 발송
        String fcmToken = fcmTokenDao.getFcmToken(username);
        if (fcmToken != null) {
            fcmService.sendMessage(fcmToken, "알림", message);
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

}
