package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.dto.hyepin.ShareDto;
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

    //유저 ID로 전체 캘린더 받아오기
    public List<CalendarDto> getCalendarByUsername(String username) {
        List<CalendarDto> calendarList = calendarDao.getCalendarByUsername(username);
        return calendarList;
    }

    //개인 캘린더 등록
    public int insertCalendar(CalendarDto calendarDto) {
        int result = calendarDao.insertCalendar(calendarDto);
        System.out.println("Dao. result" + result);
        return result;
    }

    //캘린더 수정
    public int updateCalendar(CalendarDto calendarDto) {
        int result = 0;
        //내 스케줄일 때
        if(calendarDto.getScheduleType() != null){
            if(calendarDto.getScheduleType().equals("my")){
                result = calendarDao.updateCalendar(calendarDto);
                //공유받은 일정일 때, 공유 테이블에서 수정
            }else if(calendarDto.getScheduleType().equals("oth")){
                result = calendarDao.updateShareCalendar(calendarDto);
            }
        }
        System.out.println("Dao. result = " + result);
        return result;
    }

    //캘린더 삭제
    public int deleteCalendar(CalendarDto calendarDto) {
        int result = 0;
        //내 스케줄일 때
        if(calendarDto.getScheduleType().equals("my")){
             //일정 테이블에서 삭제 (CASCADE 설정해놓음)
            result = calendarDao.deleteCalendar(calendarDto);
            //공유받은 일정일 때, 공유 테이블에서만 삭제
        }else if(calendarDto.getScheduleType().equals("oth")){
            result = calendarDao.deleteShareCalendar(calendarDto);
        }
        System.out.println("Dao. result = " + result);
        return result;
    }

    //캘린더 공유 등록
    public int shareCalendar(CalendarDto calendarDto) {
        int result = calendarDao.shareCaleandar(calendarDto);
        //채팅방으로 연결 -> 채팅방에 일정이 공유됨. 일정번호로 공유 컬럼 Y 업데이트
        //상대방 알림 테이블에 등록(알림유형 - C / 구분코드 - SH(calendar_code)
        return result;
    }

    //공유받은 캘린더 등록
    public int insertSharedCalendar(ShareDto shareDto) {
        int result = calendarDao.insertShareCalendar(shareDto);
        System.out.println("Dao. result" + result);
        return result;
    }

}
