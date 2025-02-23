package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.service.rim.NotificationService;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AlarmDao alarmDao;

    @PostMapping("/test")
    public ResponseEntity<Void> sendTestNotification(
            @RequestParam String username,
            @RequestParam String message) {

        notificationService.sendNotification(
                username,
                message,
                "TEST",  // 알림 타입,
                "test"
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications")
    public List<AlarmDto> getNotifications(
            @RequestParam String username) {
        List<AlarmDto> alarm = alarmDao.getAlarms(username);
        log.info(alarm.toString());
        return alarm;
    }

    //읽음 처리
    @PostMapping("/mark-read/{alarmId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long alarmId) {
        alarmDao.markAlarmAsRead(alarmId);
        return ResponseEntity.ok().build();
    }
}