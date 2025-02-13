package com.dogpaws.backend.repository.dao.cys;

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
}

