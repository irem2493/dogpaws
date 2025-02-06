package com.dogpaws.backend.utils;

import com.dogpaws.backend.dto.hyepin.DogMatchDto;

import java.util.function.BiPredicate;

public class DogMatchCondition {
    String name; // 조건 이름
    BiPredicate<DogMatchDto, DogMatchDto> condition;  // 비교할 조건 (두 강아지 객체를 비교)

    // 기본 생성할 때 DogMatchDto 두 객체를 비교하는 조건 (boolean 타입)
    public DogMatchCondition(String name, BiPredicate<DogMatchDto, DogMatchDto> condition) {
        this.name = name;
        this.condition = condition;
    }

    public boolean isMatched(DogMatchDto candidate, DogMatchDto userDog) {
        return condition.test(candidate, userDog);
    }
}
