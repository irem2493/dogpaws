package com.dogpaws.backend.service.cys;

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
        System.out.println("Dao. result" + result);
        return result;
    }


}
