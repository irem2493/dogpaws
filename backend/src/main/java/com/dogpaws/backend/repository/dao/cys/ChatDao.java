package com.dogpaws.backend.repository.dao.cys;

import com.dogpaws.backend.dto.cys.CalendarSharedDto;
import com.dogpaws.backend.dto.cys.CalendarSharedResponseDto;
import com.dogpaws.backend.dto.cys.DogResponseDto;
import com.dogpaws.backend.dto.hyepin.CalendarDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created on 2025-02-11 by 최윤서
 */
@Mapper
public interface ChatDao {
    public DogResponseDto getChatProfile(@Param("id") int id);
    public int insertCalendar(@Param("calenderDto") CalendarDto calendarDto);

    CalendarDto getCalendar(@Param("calendarId") int calenderId);

    void sharedSchedule(@Param("sharedDto") CalendarSharedDto sharedDto);
    void updateSharedYN(@Param("calendarId") int calenderId);

    List<String> getAllMediaUrl(@Param("roomId") String roomId);

    List<CalendarSharedResponseDto> getSharedCalendar(String roomId);
<<<<<<< HEAD

    void registStr(@Param("userStar") String userStar, @Param("reviewerId") String reviewerId, @Param("recipientId") String recipientId);
    int getCntStrById (@Param("reviewerId") String reviewerId, @Param("recipientId") String recipientId);
    Double getStrById(@Param("reviewerId") String reviewerId, @Param("recipientId") String recipientId);

=======
>>>>>>> origin/REQ-68-관리자
}

