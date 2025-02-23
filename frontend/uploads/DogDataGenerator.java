package com.dogpaws.backend.utils;


import com.dogpaws.backend.dto.hyepin.TestDogDto;

import java.util.*;

public class DogDataGenerator {

    private static Random random = new Random();

    private static String getRandomTime() {
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private static String getRandomYN() {
        return random.nextBoolean() ? "Y" : "N";
    }

    private static List<String> getRandomList(String[] source, int count) {
        Set<Integer> selected = new HashSet<>();
        List<String> result = new ArrayList<>();
        while (selected.size() < count) {
            int idx = random.nextInt(source.length);
            if (selected.add(idx)) {
                result.add(source[idx]);
            }
        }
        return result;
    }

    public static List<TestDogDto> generateDogList() {
        List<TestDogDto> dogList = new ArrayList<>();

        String[] dogNames = {"코코", "보리", "콩이", "초코", "두부", "호두", "별이", "사랑이", "까미", "몽이",
                "토리", "마루", "구름", "뭉치", "해피", "꼬미", "망고", "똘이", "봄", "쿠키",
                "사랑", "콩", "둥이", "모찌", "벌", "만두", "봄이", "초롱이", "아리", "하루",
                "레오", "뚱이", "루이", "감자", "토토", "구름이", "가을", "송이", "모모", "땅콩",
                "루비", "짱아", "흰둥이", "뽀미", "미미", "장군", "아롱이", "두리", "여름", "밍키"};

        String[] personalitySource = {"AC", "QI", "SO", "CA", "IN", "CU", "FO", "FI", "ST", "PE",
                "AR", "TR", "WE", "SE", "FA", "CO", "MA", "BR", "LA", "DO",
                "ME", "DE", "TA", "FR", "AG", "RE", "CR"};

        // 새 놀이 활동 배열
        String[] playActivities = {"BO", "WA", "SW", "PU", "RU", "HI", "HU", "RE", "FR", "JA",
                "BA", "FE", "LO", "PO", "BI", "CA", "GR", "TI", "AG", "GO",
                "CH", "SC", "ZO", "SA", "LA", "DU", "HY"};

        String[] usernames = {"user1", "user10", "user11", "user12", "user13", "user14", "user15", "user16", "user17", "user18",
                "user19", "user2", "user20", "user21", "user22", "user23", "user24", "user25", "user26", "user27",
                "user28", "user29", "user3", "user30", "user4", "user5", "user6", "user7", "user8", "user9"};

        String[] breeds = {"JD", "PBG", "JJ", "CNS", "HSD", "MHL", "PGS", "PG", "BE", "CC",
                "ES", "LFG", "SBL", "HTH", "CG", "CB", "WH", "SG", "RT", "PS",
                "AB", "PB", "MS", "WL", "BJ", "RM", "AL", "HTT", "GWT", "MT",
                "PD", "PO", "SC", "HH", "SS", "MC", "HR", "TP", "LP", "RO",
                "BN", "WA", "TS", "PN", "SK"};

        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        List<String> walkDaysList = new ArrayList<>();
        for (int i = 1; i < (1 << days.length); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < days.length; j++) {
                if ((i & (1 << j)) != 0) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(days[j]);
                }
            }
            walkDaysList.add(sb.toString());
        }
        String[] walkDaysArray = walkDaysList.toArray(new String[0]);

        for (int i = 0; i < dogNames.length; i++) {
            TestDogDto dto = new TestDogDto();
            dto.setDogName(dogNames[i]);
            dto.setUsername(usernames[i % usernames.length]);
            dto.setBreed(breeds[random.nextInt(breeds.length)]);
            dto.setIsMix(getRandomYN());

            int year = random.nextInt(2025 - 2009 + 1) + 2009;
            dto.setBirthYear(String.valueOf(year));

            int month = random.nextInt(12) + 1;
            dto.setBirthMonth(String.format("%02d", month));

            dto.setGender(random.nextBoolean() ? "M" : "F");
            dto.setIsNeutered(getRandomYN());
            dto.setWeight(random.nextInt(20 - 3 + 1) + 3);
            dto.setWalkStartTime(getRandomTime());
            dto.setWalkEndTime(getRandomTime());
            dto.setWalkDays(walkDaysArray[random.nextInt(walkDaysArray.length)]);
            dto.setIsMatingAvailable(getRandomYN());
            int personalityCount = random.nextInt(5) + 1;
            dto.setPersonalityTypes(getRandomList(personalitySource, personalityCount));
            int playCount = random.nextInt(5) + 1;
            dto.setPlayList(getRandomList(playActivities, playCount));
            dto.setProfileUrl("http://localhost:2000/uploads/" + (i + 1) + ".jpg");
            dto.setBloodTestCertified(getRandomYN());
            dto.setVaccinationCertified(getRandomYN());
            dto.setHealthRecordCertified(getRandomYN());

            dogList.add(dto);
        }
        return dogList;
    }
}