package com.dogpaws.backend.repository.dao.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarDao {

    public List<CalendarDto> getCalendarByUsername(@Param("username") String username);
    public int insertCalendar(CalendarDto calendarDto);
    public int updateCalendar(CalendarDto calendarDto);
    public int deleteCalendar(CalendarDto calendarDto);
    public int updateShareCalendar(CalendarDto calendarDto);
    public int deleteShareCalendar(CalendarDto calendarDto);
}
