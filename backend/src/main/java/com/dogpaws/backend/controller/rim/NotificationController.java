package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.repository.dao.common.AlarmDao;
import com.dogpaws.backend.service.rim.NotificationService;
import com.dogpaws.frontend.dto.hyepin.AlarmDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return alarmDao.getAlarms(username);
    }
}