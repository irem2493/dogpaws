package com.dogpaws.frontend.utils;


import com.dogpaws.frontend.dto.hyepin.FilterDto;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StringUtil {
    
    //문자열 잘라서 리스트로 만들기
    public static List<String> splitToList(String str) {
        return Optional.ofNullable(str)
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .toList()) // Java 16 이상이면 Collectors.toList() 대신 .toList() 사용 가능
                .orElse(List.of());
    }

    // 시간 변환 로직 (17:00:00 ~ 17:30:00 → 17:00 ~ 17:30)
    public static String formatTime(LocalTime time) {
        return Optional.ofNullable(time)
                .map(t -> t.format(DateTimeFormatter.ofPattern("HH:mm"))) // 초 없이 시:분 형식으로 포맷
                .orElse(""); // null일 경우 빈 문자열 반환
    }

    //하나의 문자열로 만들기
    public static String joinListToString(List<String> list) {
        if(list == null || list.isEmpty()){
            return "";
        }
        return String.join(",", list);
    }
    
}