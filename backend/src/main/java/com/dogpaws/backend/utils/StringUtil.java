package com.dogpaws.backend.utils;

import com.dogpaws.backend.dto.hyepin.FilterDto;

import java.lang.reflect.Field;
import java.time.LocalDate;
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

    //리스트 각 변수에 할당
    public static FilterDto listToString(FilterDto filterDto) {
        // dogPersonalList가 null일 경우 빈 리스트로 초기화
        if (filterDto.getDogPersonalGbnCdList() == null) {
            filterDto.setDogPersonalGbnCdList(new ArrayList<>()); // 빈 리스트로 초기화
        }

        // dogPlayList가 null일 경우 빈 리스트로 초기화
        if (filterDto.getDogPlayGbnCdList() == null) {
            filterDto.setDogPlayGbnCdList(new ArrayList<>()); // 빈 리스트로 초기화
        }

        // dogPersonalList의 크기를 5로 맞추기 위해 null 값을 추가
        while (filterDto.getDogPersonalGbnCdList().size() < 5) {
            filterDto.getDogPersonalGbnCdList().add(null); // 리스트 크기를 5로 맞추기 위해 null 값 추가
        }

        // dogPlayGbnCdList의 크기를 5로 맞추기 위해 null 값을 추가
        while (filterDto.getDogPlayGbnCdList().size() < 5) {
            filterDto.getDogPlayGbnCdList().add(null); // 리스트 크기를 5로 맞추기 위해 null 값 추가
        }

        // dogPersonalList와 dogPlayGbnCdList를 동적으로 설정
        for (int i = 0; i < 5; i++) {
            String personalFieldName = "dogPersonalGbnCd" + (i + 1);
            String playFieldName = "dogPlayGbnCd" + (i + 1);

            String personalValue = filterDto.getDogPersonalGbnCdList().get(i);
            String playValue = filterDto.getDogPlayGbnCdList().get(i); // dogPlayGbnCdList에 대해서도 동일한 작업

            try {
                // 필드명에 해당하는 필드 객체 가져오기
                Field personalField = filterDto.getClass().getDeclaredField(personalFieldName);
                Field playField = filterDto.getClass().getDeclaredField(playFieldName);

                // 필드 접근 가능하게 설정
                personalField.setAccessible(true);
                playField.setAccessible(true);

                // 필드에 값 설정
                personalField.set(filterDto, personalValue);
                playField.set(filterDto, playValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return filterDto;
    }

    /**
     * String 타입의 날짜를 LocalDate로 변환
     * @param dateStr 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 변환된 LocalDate 객체, 유효하지 않은 입력의 경우 null 반환
     */
    public static LocalDate stringToLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}