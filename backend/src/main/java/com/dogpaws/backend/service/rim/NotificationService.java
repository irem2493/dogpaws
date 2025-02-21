package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.repository.dao.rim.FCMTokenDao;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final FCMService fcmService;

    private final FCMTokenDao fcmTokenDao;

    private final AlarmDao alarmDao;

    private final UserRepository userRepository;


    public void sendNotification(String username, String message, String alarmType) {
        log.info("알림 저장 시작: username={}, type={}, message={}", username, alarmType, message);


        log.info("알림 저장 완료: username={}, type={}", username, alarmType);

        // FCM 토큰 조회 후 알림 전송
        String fcmToken = getFcmToken(username);
        if (fcmToken != null) {
            fcmService.sendMessage(fcmToken, "알림", message);
        }else{
            log.info("FCM 토큰 조회 실패.......... ");
        }

    }
    private String getFcmToken(String username) {
        try {
            return fcmTokenDao.getFcmToken(username);
        } catch (Exception e) {
            log.error("FCM 토큰 조회 실패: " + e.getMessage());
            return null;
        }
    }
    // 전체 발송 메서드 추가
    public void sendNotificationToAll(String message, String alarmType) {
        userRepository.findAll().forEach(user -> {
            sendNotification(user.getUsername(), message, alarmType);
        });
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
}
