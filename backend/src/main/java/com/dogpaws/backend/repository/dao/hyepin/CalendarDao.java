package com.dogpaws.backend.repository.dao.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.dto.hyepin.ShareDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarDao {

    public List<CalendarDto> getCalendarByUsername(@Param("username") String username);
    public Integer insertCalendar(CalendarDto calendarDto);
    public Integer updateCalendar(CalendarDto calendarDto);
    public Integer deleteCalendar(CalendarDto calendarDto);

    public Integer shareCaleandar(CalendarDto calendarDto);

    public Integer insertShareCalendar(ShareDto shareDto);
    public Integer updateShareCalendar(CalendarDto calendarDto);
    public Integer deleteShareCalendar(CalendarDto calendarDto);

    //알림 - rim
    Long getLastInsertedCalendarId(@Param("username") String username);
}
