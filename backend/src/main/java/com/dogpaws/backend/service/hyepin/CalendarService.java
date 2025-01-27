package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.repository.dao.hyepin.CalendarDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarDao calendarDao;

    public List<CalendarDto> getCalendarByUsername(String username) {
        List<CalendarDto> calendarList = calendarDao.getCalendarByUsername(username);
        return calendarList;
    }

}
