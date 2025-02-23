package com.dogpaws.backend.service.cys;

import com.dogpaws.backend.dto.cys.CalendarSharedDto;
import com.dogpaws.backend.dto.cys.CalendarSharedResponseDto;
import com.dogpaws.backend.dto.cys.DogResponseDto;
import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.repository.dao.cys.ChatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2025-02-11 by 최윤서
 */
@Service
public class ChatService {

    @Autowired
    private ChatDao chatDao;

    //profile 불러오기
    public List<DogResponseDto> getChatProfile(List<Integer> otherParticipants) {
        List<DogResponseDto> profiles = new ArrayList<>();

        for (Integer otherParticipant : otherParticipants) {
            DogResponseDto profile = chatDao.getChatProfile(otherParticipant);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        return profiles;
    };

    public int insertSchedule(CalendarDto calendarDto){
        int result = chatDao.insertCalendar(calendarDto);
        if (result == 1) {
            int calendarId = calendarDto.getCalendarId();
            System.out.println("service. calenderId" + calendarId);
            return calendarId;
        }
        return 0;
    }


    public CalendarDto getSchedule(int calenderId) {
        CalendarDto dto = chatDao.getCalendar(calenderId);
        return dto;
    }

    public void sharedSchedule(CalendarSharedDto sharedDto) {
        chatDao.sharedSchedule(sharedDto);
        chatDao.updateSharedYN(sharedDto.getCalendarId());
    }


    public List<String> getAllMediaUrl(String roomId) {
        return chatDao.getAllMediaUrl(roomId);
    }

    public List<CalendarSharedResponseDto> getSharedCalendar(String roomId) {
        return chatDao.getSharedCalendar(roomId);
    }

    public void registStr(String userStar, String reviewerId, String recipientId) {
        chatDao.registStr(userStar, reviewerId, recipientId);
    }

    public int getCntStrById(String reviewerId, String recipientId) {
        return chatDao.getCntStrById(reviewerId, recipientId);
    }

    public Double getStrById(String reviewerId, String recipientId) {
        Double rating =  chatDao.getStrById(reviewerId, recipientId);
        return (rating != null) ? rating : 0.0; // ✅ NULL이면 0.0 반환
    }

}
